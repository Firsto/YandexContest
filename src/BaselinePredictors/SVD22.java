package BaselinePredictors;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SVD22 {

    int MAX_RATINGS = 1000001; // Ratings in entire training set (+1)
    int MAX_CUSTOMERS = 10001; // Customers in the entire training set (+1)
    int MAX_MOVIES = 10001; // Movies in the entire training set (+1)
    int MAX_FEATURES = 10; // Number of features to use
    int MIN_EPOCHS = 3; // Minimum number of epochs per feature
    int MAX_EPOCHS = 200; // Max epochs per feature

    double MIN_IMPROVEMENT = 0.0001; // Minimum improvement required to continue current feature
    double INIT = 0.1; // Initialization value for features
    double LRATE = 0.001; // Learning rate parameter
    double K = 0.015; // Regularization parameter used to minimize over-fitting

    //HashMap<Integer, Integer> IdMap;
    //Iterator IdItr = IdMap.entrySet().iterator();

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
        byte Rating;
        float Cache;
    };



        int m_nRatingCount; // Current number of loaded ratings
        Data_[] m_aRatings = new Data_[MAX_RATINGS]; // Array of ratings data
        Movie[] m_aMovies = new Movie[MAX_MOVIES]; // Array of movie metrics
        Customer[] m_aCustomers = new Customer[MAX_CUSTOMERS]; // Array of customer metrics
        float[][] m_aMovieFeatures = new float[MAX_FEATURES][MAX_MOVIES]; // Array of features by movie (using floats to save space)
        float[][] m_aCustFeatures = new float [MAX_FEATURES][MAX_CUSTOMERS]; // Array of features by customer (using floats to save space)
        Map<Integer, Integer> m_mCustIds; // Map for one time translation of ids to compact array index
        float glAverage;

//        double PredictRating(short movieId, int custId, int feature, float cache, boolean bTrailing=true);
//        double PredictRating(short movieId, int custId);

//bool ReadNumber(wchar_t* pwzBufferIn, int nLength, int &nPosition, wchar_t* pwzBufferOut);
//bool ParseInt(wchar_t* pwzBuffer, int nLength, int &nPosition, int& nValue);
//bool ParseFloat(wchar_t* pwzBuffer, int nLength, int &nPosition, float& fValue);

//        public:
//        Engine(void);
//        ~Engine(void) { };
//
//        void CalcMetrics();
//        void CalcFeatures();
//        void LoadHistory();
//        void SaveFeatures();
//        void ProcessTest();
//void ProcessTest(wchar_t* pwzFile);
//void ProcessFile(wchar_t* pwzFile);


    void init()
    {
        m_aRatings = new Data_[MAX_RATINGS]; // Array of ratings data
        m_aMovies = new Movie[MAX_MOVIES]; // Array of movie metrics
        m_aCustomers = new Customer[MAX_CUSTOMERS]; // Array of customer metrics
        m_aMovieFeatures = new float[MAX_FEATURES][MAX_MOVIES]; // Array of features by movie (using floats to save space)
        m_aCustFeatures = new float [MAX_FEATURES][MAX_CUSTOMERS];
        m_mCustIds = new HashMap<>();
        m_nRatingCount = 0;

        for (int f=0; f<MAX_FEATURES; f++)
        {
            for (int i=0; i<MAX_MOVIES; i++) m_aMovieFeatures[f][i] = (float)INIT;
            for (int i=0; i<MAX_CUSTOMERS; i++) m_aCustFeatures[f][i] = (float)INIT;
        }
    }

//-------------------------------------------------------------------
// Data Loading / Saving
//-------------------------------------------------------------------
    int k,u,m,d,t,ui,mi;
    String[] rec;
    //
// LoadHistory
// - Loop through all of the files in the training directory
//
    void LoadHistory()
    {
//TODO:
        int movieId, custId, rating;
//        Arrays.fill(m_aRatings, new Data_());


        glAverage = 0;
//        System.out.println("Hi!");

        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            String str = "";

            str = rdr.readLine();
            try{
            k = Integer.parseInt(str.split(" ")[0]);
            u = Integer.parseInt(str.split(" ")[1]);
            m = Integer.parseInt(str.split(" ")[2]);
            d = Integer.parseInt(str.split(" ")[3]);
            t = Integer.parseInt(str.split(" ")[4]);
            } catch(Exception e) {System.exit(0);}
            MAX_CUSTOMERS = u;
            MAX_MOVIES = m;
            MAX_RATINGS = d;


            for (int i = 0; i < MAX_RATINGS; i++) {
                m_aRatings[i] = new Data_();
                m_aRatings[i].CustId = 0;
                m_aRatings[i].MovieId = 0;
                m_aRatings[i].Rating = 0;
                m_aRatings[i].Cache = 0;
            }

            for (int i = 0; i<MAX_MOVIES; i++) {
                m_aMovies[i] = new Movie();
                m_aMovies[i].RatingCount = 0;
                m_aMovies[i].RatingSum = 0;
            }

            for (int i = 0; i<MAX_CUSTOMERS; i++) {
                m_aCustomers[i] = new Customer();
                m_aCustomers[i].RatingCount = 0;
                m_aCustomers[i].RatingSum = 0;
                m_aCustomers[i].CustomerId = i;
            }


//            MAX_FEATURES = 1;
                rec = new String[t];
                int value;
                if (d>0) for (int i = 0; i < d; i++) {
                    str = rdr.readLine();
                    ui=0;mi=0;value = 0;
                    try {
                    ui = Integer.parseInt(str.split(" ")[0]);
                    mi = Integer.parseInt(str.split(" ")[1]);
                    value = (int) Double.parseDouble(str.split(" ")[2]);
                    } catch(Exception e) {System.exit(0);}
                    m_aRatings[m_nRatingCount].MovieId = (short)mi;
                    m_aRatings[m_nRatingCount].CustId = ui;
                    m_aRatings[m_nRatingCount].Rating = (byte)value;
                    m_aRatings[m_nRatingCount].Cache = 0;
//                    System.out.println(m_nRatingCount);
//                    System.out.println(m_aRatings[m_nRatingCount].CustId + " : " + m_aRatings[m_nRatingCount].MovieId + " : " + m_aRatings[m_nRatingCount].Rating);
//                    System.out.println("rating #" + i + " --> " + m_aRatings[i].CustId + " : " + m_aRatings[i].MovieId + " : " + m_aRatings[i].Rating + " : cache " + m_aRatings[i].Cache);
                    m_nRatingCount++;
                    glAverage += value;
                }
                if (t>0) for (int i = 0; i < t; i++) {
                    str = rdr.readLine();
                    rec[i] = str;
//                ui = Integer.parseInt(str.split(" ")[0]);
//                mi = Integer.parseInt(str.split(" ")[1]);
                }
            rdr.close();

            glAverage /= (MAX_RATINGS-1);

        }

        catch(IOException e)
        {
//            System.out.println("DAFUQ U WRITIN");
            System.exit(0);
        }
    }

    void CalcMetrics()
    {
        int i, cid;
        Iterator itr;

//        System.out.println("Calculating intermediate metrics");

// Process each row in the training set
        for (i = 0; i<m_nRatingCount; i++)
        {

            Data_ rating = m_aRatings[i];

//            System.out.println("rating #" + i + " --> " + m_aRatings[i].CustId + " : " + m_aRatings[i].MovieId + " : " + m_aRatings[i].Rating + " : cache " + m_aRatings[i].Cache);
// Increment movie stats
            m_aMovies[rating.MovieId].RatingCount++;
            m_aMovies[rating.MovieId].RatingSum += rating.Rating;
            cid = rating.CustId;
            m_aCustomers[cid].RatingCount++;
            m_aCustomers[cid].RatingSum += rating.Rating;
// Add customers (using a map to re-number id's to array indexes)

//            itr m_mCustIds.find(rating.CustId);
//            itr = m_mCustIds.keySet().iterator();
            /*
            itr = m_mCustIds.entrySet().iterator();

            if (!itr.hasNext())
            {
                cid = 1 + (int)m_mCustIds.size();

// Reserve new id and add lookup
                m_mCustIds.put(rating.CustId,cid);
//                m_mCustIds[rating.CustId] = cid;

// Store off old sparse id for later
                m_aCustomers[cid].CustomerId = rating.CustId;

// Init vars to zero
                m_aCustomers[cid].RatingCount = 0;
                m_aCustomers[cid].RatingSum = 0;
            }
            else
            {
                cid = (int)m_mCustIds.entrySet().iterator().next().getKey();
//                cid = 0 ;
            }
// Swap sparse id for compact one
//            rating.CustId = cid;
//            System.out.println(cid);
//            cid = rating.CustId;
//            m_aCustomers[cid].CustomerId = rating.CustId;
            m_aCustomers[cid].RatingCount++;
            m_aCustomers[cid].RatingSum += rating.Rating;
            */
        }

// Do a follow-up loop to calc movie averages
        for (i = 0; i<MAX_MOVIES; i++)
        {
            Movie movie = m_aMovies[i];
            movie.RatingAvg = movie.RatingSum / (1.0 * movie.RatingCount);
            movie.PseudoAvg = (3.23 * 25.0 + movie.RatingSum) / (25.0 + movie.RatingCount);
//            m_aMovies[i]=movie;
//            System.out.println(m_aMovies[i].PseudoAvg);
//            System.out.println(movie.RatingAvg);
        }

        for (i = 0; i<MAX_CUSTOMERS; i++) {
            Customer cust = m_aCustomers[i]; //m_aCustomers[i].RatingCount = 0;
            cust.RatingAvg = cust.RatingSum / (1.0 * cust.RatingCount);
            cust.PseudoAvg = (3.23 * 25.0 + cust.RatingSum) / (25.0 + cust.RatingCount);
//            System.out.println(m_aCustomers[i].PseudoAvg);
//            m_aCustomers[i]=cust;
        }


    }

    void CalcFeatures()
    {
        int f, e, i, custId, cnt = 0;
        Data_ rating;
        double err, p, sq, rmse_last=0, rmse = 2.0;
        short movieId;
        float cf, mf;


        for (f=0; f<MAX_FEATURES; f++)
        {
//            System.out.printf("- %d - \n", f);

// Keep looping until you have passed a minimum number
// of epochs or have stopped making significant progress
            for (e=0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
            {
                cnt++;
                sq = 0;
                rmse_last = rmse;

                for (i=0; i<m_nRatingCount; i++)
                {
                    rating = m_aRatings[i];
                    movieId = rating.MovieId;
                    custId = rating.CustId;

// Predict rating and calc error
                    p = PredictRating(movieId, custId, f, rating.Cache, true);
//                    System.out.println(p);
//                    err = (1.0 * rating.Rating - (glAverage+(glAverage-m_aMovies[movieId].PseudoAvg)+(glAverage-m_aCustomers[custId].PseudoAvg)) - p);
                    err = (1.0 * rating.Rating - p);
                    sq += err*err;

// Cache off old feature values
                    cf = m_aCustFeatures[f][custId];
                    mf = m_aMovieFeatures[f][movieId];

// Cross-train the features
                    m_aCustFeatures[f][custId] += (float)(LRATE * (err * mf - K * cf));
                    m_aMovieFeatures[f][movieId] += (float)(LRATE * (err * cf - K * mf));
                }

//                rmse = Math.sqrt(sq / m_nRatingCount);
                rmse = Math.sqrt(sq / m_nRatingCount);

//                System.out.printf(" <set x='%d' y='%f' /> \n", cnt, rmse);
            }

// Cache off old predictions
            for (i=0; i<m_nRatingCount; i++)
            {
                rating = m_aRatings[i];
//                System.out.println("oldcache: " + rating.Cache);
                rating.Cache = (float)PredictRating(rating.MovieId, rating.CustId, f, rating.Cache, false);
//                System.out.println("RC : " + rating.Cache);
            }

        }
    }

    void ProcessTest() {

        double predicted;
        int movieId, custId, rating;
        double rmse = 0.0;


        for (int i = 0; i < t; i++) {
            ui = Integer.parseInt(rec[i].split(" ")[0]);
            mi = Integer.parseInt(rec[i].split(" ")[1]);

                custId = ui;
                movieId = mi;

                rating = 0;
//                predicted = glAverage+(glAverage-m_aMovies[movieId].PseudoAvg)+(glAverage-m_aCustomers[custId].PseudoAvg)+PredictRating((short)movieId, custId);
//            System.out.println("GLAVERAGE " + glAverage);

            predicted = PredictRating((short)movieId, custId);
                rmse += Math.pow(predicted - rating, 2);
//            System.out.println(rmse);
//            System.out.println("Predicted: " + PredictRating((short)movieId,custId));
//            System.out.println(PredictRating((short)movieId,custId));
//                System.out.println("Customer: " + custId+", Movie: "+movieId+", Rating: "+predicted);
                System.out.println(predicted);
        }


            rmse = Math.sqrt(rmse / m_nRatingCount);

//            System.out.println("RMSE = "+rmse);


    }



    void SaveFeatures (){

        int i,j;

        for (i = 0; i<MAX_FEATURES; i++) {
            for (j=1; j<MAX_CUSTOMERS; j++)
                System.out.println(j+","+i+","+m_aCustFeatures[i][j]);
            for (j=1; j<MAX_MOVIES; j++)
                System.out.println(j+","+i+","+m_aMovieFeatures[i][j]);
        }


    }

    double PredictRating(short movieId, int custId, int feature, float cache, boolean bTrailing)
    {
// Get cached value for old features or default to an average
        double sum = (cache > 0) ? cache : 1; //m_aMovies[movieId].PseudoAvg;

// Add contribution of current feature
        sum += m_aMovieFeatures[feature][movieId] * m_aCustFeatures[feature][custId];
        if (sum > 10) sum = 10;
        if (sum < 0) sum = 0;

// Add up trailing defaults values
        if (bTrailing)
        {
            sum += (MAX_FEATURES-feature-1) * (INIT * INIT);
            if (sum > 10) sum = 10;
            if (sum < 0) sum = 0;
        }

        return sum;
    }

//
// PredictRating
// - This version is used for calculating the final results
// - It loops through the entire list of finished features
//

    double PredictRating(short movieId, int custId)
    {
        double sum = 1; //m_aMovies[movieId].PseudoAvg;

        for (int f=0; f<MAX_FEATURES; f++)
        {
            sum += m_aMovieFeatures[f][movieId] * m_aCustFeatures[f][custId];
            if (sum > 10) sum = 10;
            if (sum < 0) sum = 0;
        }

        return sum;
    }



    public static void main(String[] args) throws IOException {
        SVD22 engine = new SVD22();

/*        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        String str = "";

        str = rdr.readLine();
        engine.k = Integer.parseInt(str.split(" ")[0]);
        engine.u = Integer.parseInt(str.split(" ")[1]);
        engine.m = Integer.parseInt(str.split(" ")[2]);
        engine.d = Integer.parseInt(str.split(" ")[3]);
        engine.t = Integer.parseInt(str.split(" ")[4]);
        engine.MAX_CUSTOMERS = engine.u;
        engine.MAX_MOVIES = engine.m;
        engine.MAX_FEATURES = 1;
        */

        engine.init();
        engine.LoadHistory();
        engine.CalcMetrics();
        engine.CalcFeatures();
        engine.ProcessTest();
//        engine.SaveFeatures();
        /*
        for (int i = 0; i < engine.m_nRatingCount; i++) {
            System.out.println(" --- " + i);
            System.out.print(engine.m_aRatings[i].CustId + " : ");
            System.out.print(engine.m_aRatings[i].MovieId + " : ");
            System.out.print(engine.m_aRatings[i].Rating + " : ");
            System.out.println(engine.m_aRatings[i].Cache);
        }
        */
    }

}
