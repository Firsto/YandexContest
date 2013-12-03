$(document).ready(function(){

    $('.quiz a.answer').click (function(){

        var answerIndex = $(this).attr ('data-id');

        $.get (
            '/ajax/quizAnswer',
            {
                answerIndex: answerIndex
            },
            function(data){
                $('#quizContainer').html (data);
            }
        );

        return false;
    });

});