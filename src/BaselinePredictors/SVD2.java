/*
package BaselinePredictors;


public class SVD2 {

    #include "stdafx.h"
            #include <math.h>
    #include <tchar.h>
    #include <map>

    using namespace std;
    using namespace System;
    using namespace System::IO;

    #define MAX_RATINGS 99001 // Ratings in entire training set (+1)
            #define MAX_CUSTOMERS 944 // Customers in the entire training set (+1)
            #define MAX_MOVIES 1683 // Movies in the entire training set (+1)
            #define MAX_FEATURES 50 // Number of features to use
            #define MIN_EPOCHS 3 // Minimum number of epochs per feature
            #define MAX_EPOCHS 200 // Max epochs per feature

            #define MIN_IMPROVEMENT 0.0001 // Minimum improvement required to continue current feature
            #define INIT 0.1 // Initialization value for features
            #define LRATE 0.001 // Learning rate parameter
            #define K 0.015 // Regularization parameter used to minimize over-fitting

    typedef unsigned char BYTE;
    typedef map<int, int> IdMap;
    typedef IdMap::iterator IdItr;

    class Movie
    {
        int RatingCount;
        int RatingSum;
        double RatingAvg;
        double PseudoAvg; // Weighted average used to deal with small movie counts
    };

    class Customer
    {
        int CustomerId;
        int RatingCount;
        int RatingSum;
        double RatingAvg;
        double PseudoAvg;
    };

    class Data_
    {
        int CustId;
        short MovieId;
        BYTE Rating;
        float Cache;
    };

    class Engine
    {
        private:
        int m_nRatingCount; // Current number of loaded ratings
        Data_ m_aRatings[MAX_RATINGS]; // Array of ratings data
        Movie m_aMovies[MAX_MOVIES]; // Array of movie metrics
        Customer m_aCustomers[MAX_CUSTOMERS]; // Array of customer metrics
        float m_aMovieFeatures[MAX_FEATURES][MAX_MOVIES]; // Array of features by movie (using floats to save space)
        float m_aCustFeatures[MAX_FEATURES][MAX_CUSTOMERS]; // Array of features by customer (using floats to save space)
        IdMap m_mCustIds; // Map for one time translation of ids to compact array index
        float glAverage;

        inline double PredictRating(short movieId, int custId, int feature, float cache, bool bTrailing=true);
        inline double PredictRating(short movieId, int custId);

//bool ReadNumber(wchar_t* pwzBufferIn, int nLength, int &nPosition, wchar_t* pwzBufferOut);
//bool ParseInt(wchar_t* pwzBuffer, int nLength, int &nPosition, int& nValue);
//bool ParseFloat(wchar_t* pwzBuffer, int nLength, int &nPosition, float& fValue);

        public:
        Engine(void);
        ~Engine(void) { };

        void CalcMetrics();
        void CalcFeatures();
        void LoadHistory();
        void SaveFeatures();
        void ProcessTest();
//void ProcessTest(wchar_t* pwzFile);
//void ProcessFile(wchar_t* pwzFile);
    };

    Engine::Engine(void)
    {
        m_nRatingCount = 0;

        for (int f=0; f<MAX_FEATURES; f++)
        {
            for (int i=0; i<MAX_MOVIES; i++) m_aMovieFeatures[f] = (float)INIT;
            for (int i=0; i<MAX_CUSTOMERS; i++) m_aCustFeatures[f] = (float)INIT;
        }
    }

//-------------------------------------------------------------------
// Data Loading / Saving
//-------------------------------------------------------------------

    //
// LoadHistory
// - Loop through all of the files in the training directory
//
    void Engine::LoadHistory()
    {
//TODO:
        int movieId, custId, rating;

        for (int i = 0; i<MAX_MOVIES; i++) {
            m_aMovies.RatingCount = 0;
            m_aMovies.RatingSum = 0;
        }

        for (int i = 0; i<MAX_CUSTOMERS; i++) {
            m_aCustomers.RatingCount = 0;
            m_aCustomers.RatingSum = 0;
            m_aCustomers.CustomerId = i;
        }

        glAverage = 0;
        Console::WriteLine(L"Hello World");

        try {
            String^ textFile = L"C:\\r_training2.tsv";
            StreamReader^ reader = gcnew StreamReader(textFile);
            do {

                String^ st = reader.ReadLine();


                custId = Int32::Parse(st.Substring(0, st.IndexOf(L",")));
                st = st.Remove(0,st.IndexOf(L",")+1);

                movieId = Int32::Parse(st.Substring(0, st.IndexOf(L",")));
                st = st.Remove(0,st.IndexOf(L",")+1);

                rating = Int32::Parse(st.Substring(0, st.Length));

                m_aRatings[m_nRatingCount].MovieId = (short)movieId;
                m_aRatings[m_nRatingCount].CustId = custId;
                m_aRatings[m_nRatingCount].Rating = (BYTE)rating;
                m_aRatings[m_nRatingCount].Cache = 0;
                m_nRatingCount++;
                glAverage += rating;

            }

            while(reader.Peek() != -1);
            glAverage /= (MAX_RATINGS-1);

        }

        catch(FileNotFoundException^ ex)
        {
            Console::WriteLine(L"File Not Found");
        }
        catch(System::Exception^ e)
        {
            Console::WriteLine(L"System exception... ");
        }
    }

    void Engine::CalcMetrics()
    {
        int i, cid;
        IdItr itr;

        Console::WriteLine(L"Calculating intermediate metrics");

// Process each row in the training set
        for (i=0; i<m_nRatingCount; i++)
        {
            Data_* rating = m_aRatings + i;

// Increment movie stats
            m_aMovies[rating.MovieId].RatingCount++;
            m_aMovies[rating.MovieId].RatingSum += rating.Rating;

// Add customers (using a map to re-number id's to array indexes)
            itr = m_mCustIds.find(rating.CustId);
            if (itr == m_mCustIds.end())
            {
                cid = 1 + (int)m_mCustIds.size();

// Reserve new id and add lookup
                m_mCustIds[rating.CustId] = cid;

// Store off old sparse id for later
                m_aCustomers[cid].CustomerId = rating.CustId;

// Init vars to zero
                m_aCustomers[cid].RatingCount = 0;
                m_aCustomers[cid].RatingSum = 0;
            }
            else
            {
                cid = itr.second;
            }

// Swap sparse id for compact one
            rating.CustId = cid;



            cid = rating.CustId;

            m_aCustomers[cid].RatingCount++;
            m_aCustomers[cid].RatingSum += rating.Rating;
        }

// Do a follow-up loop to calc movie averages
        for (i=0; i<MAX_MOVIES; i++)
        {
            Movie* movie = m_aMovies+i;
            movie.RatingAvg = movie.RatingSum / (1.0 * movie.RatingCount);
            movie.PseudoAvg = (3.23 * 25 + movie.RatingSum) / (25.0 + movie.RatingCount);
        }

        for (int i = 0; i<MAX_CUSTOMERS; i++) {
            Customer* cust = m_aCustomers+i; m_aCustomers.RatingCount = 0;
            cust.RatingAvg = cust.RatingSum / (1.0 * cust.RatingCount);
            cust.PseudoAvg = (3.23 * 25 + cust.RatingSum) / (25.0 + cust.RatingCount);
        }

    }

    void Engine::CalcFeatures()
    {
        int f, e, i, custId, cnt = 0;
        Data_* rating;
        double err, p, sq, rmse_last, rmse = 2.0;
        short movieId;
        float cf, mf;

        for (f=0; f<MAX_FEATURES; f++)
        {
            Console::WriteLine(L"-— Calculating feature: %d —-", f);

// Keep looping until you have passed a minimum number
// of epochs or have stopped making significant progress
            for (e=0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
            {
                cnt++;
                sq = 0;
                rmse_last = rmse;

                for (i=0; i<m_nRatingCount; i++)
                {
                    rating = m_aRatings + i;
                    movieId = rating.MovieId;
                    custId = rating.CustId;

// Predict rating and calc error
                    p = PredictRating(movieId, custId, f, rating.Cache, true);
                    err = (1.0 * rating.Rating - (glAverage+(glAverage-m_aMovies[movieId].PseudoAvg)+(glAverage-m_aCustomers[custId].PseudoAvg)) - p);
                    sq += err*err;

// Cache off old feature values
                    cf = m_aCustFeatures[f][custId];
                    mf = m_aMovieFeatures[f][movieId];

// Cross-train the features
                    m_aCustFeatures[f][custId] += (float)(LRATE * (err * mf - K * cf));
                    m_aMovieFeatures[f][movieId] += (float)(LRATE * (err * cf - K * mf));
                }

                rmse = sqrt(sq/m_nRatingCount);

                Console::WriteLine(L" <set x='%d' y='%f' /> ",cnt,rmse);
            }

// Cache off old predictions
            for (i=0; i<m_nRatingCount; i++)
            {
                rating = m_aRatings + i;
                rating.Cache = (float)PredictRating(rating.MovieId, rating.CustId, f, rating.Cache, false);
            }
        }
    }

    void Engine::ProcessTest() {

        double predicted;
        int movieId, custId, rating;
        double rmse = 0.0;

        try {
            String^ ufeatures = L"C:\\output_r3.txt";
            StreamWriter^ uwr = gcnew StreamWriter(ufeatures );

            String^ textFile = L"C:\\r_probe2.tsv";
            StreamReader^ reader = gcnew StreamReader(textFile);
            do {

                String^ st = reader.ReadLine();


                custId = Int32::Parse(st.Substring(0, st.IndexOf(L",")));
                st = st.Remove(0,st.IndexOf(L",")+1);

                movieId = Int32::Parse(st.Substring(0, st.IndexOf(L",")));
                st = st.Remove(0,st.IndexOf(L",")+1);

                rating = Int32::Parse(st.Substring(0, st.Length));

                predicted = glAverage+(glAverage-m_aMovies[movieId].PseudoAvg)+(glAverage-m_aCustomers[custId].PseudoAvg)+PredictRating(movieId, custId);
                rmse += Math::Pow(predicted-rating,2);

                uwr.WriteLine(custId+","+movieId+","+predicted);

            }

            while(reader.Peek() != -1);

            rmse = sqrt(rmse / 1000);
            uwr.Close();
            Console::WriteLine(L"RMSE = "+rmse);

        }

        catch(FileNotFoundException^ ex)
        {
            Console::WriteLine(L"File Not Found");
        }
        catch(System::Exception^ e)
        {
            Console::WriteLine(L"File Not Found");
        }

    }

    void Engine::SaveFeatures (){

        int i,j;

        String^ ufeatures = L"C:\\ufeatures.csv";
        String^ mfeatures = L"C:\\mfeatures.csv";

        StreamWriter^ uwr = gcnew StreamWriter(ufeatures );
        StreamWriter^ mwr = gcnew StreamWriter(mfeatures );

        for (i = 0; i<MAX_FEATURES; i++) {
            for (j=1; j<MAX_CUSTOMERS; j++)
                uwr.WriteLine(j+","+i+","+m_aCustFeatures[j]);
            for (j=1; j<MAX_MOVIES; j++)
                mwr.WriteLine(j+","+i+","+m_aMovieFeatures[j]);
        }

        uwr.Close();
        mwr.Close();

    }

    double Engine::PredictRating(short movieId, int custId, int feature, float cache, bool bTrailing)
    {
// Get cached value for old features or default to an average
        double sum = (cache > 0) ? cache : 1; //m_aMovies[movieId].PseudoAvg;

// Add contribution of current feature
        sum += m_aMovieFeatures[feature][movieId] * m_aCustFeatures[feature][custId];
//if (sum > 5) sum = 5;
//if (sum < 1) sum = 1;

// Add up trailing defaults values
        if (bTrailing)
        {
            sum += (MAX_FEATURES-feature-1) * (INIT * INIT);
//if (sum > 5) sum = 5;
//if (sum < 1) sum = 1;
        }

        return sum;
    }

//
// PredictRating
// - This version is used for calculating the final results
// - It loops through the entire list of finished features
//

    double Engine::PredictRating(short movieId, int custId)
    {
        double sum = 1; //m_aMovies[movieId].PseudoAvg;

        for (int f=0; f<MAX_FEATURES; f++)
        {
            sum += m_aMovieFeatures[f][movieId] * m_aCustFeatures[f][custId];
//if (sum > 5) sum = 5;
//if (sum < 1) sum = 1;
        }

        return sum;
    }

    int main(array<System::String ^> ^args)
    {
        Engine* engine = new Engine();
        engine.LoadHistory();
        engine.CalcMetrics();
        engine.CalcFeatures();
        engine.ProcessTest();
        Console::ReadLine();

        return 0;
    }

}
*/
