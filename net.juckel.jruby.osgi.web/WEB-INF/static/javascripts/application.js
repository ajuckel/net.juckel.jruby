(function($) {
    var startTime = new Date();
    var successes = 0;
    var errors = 0;
    var shouldRun = true;
    var fib = function(x) {
        if (x <= 0) {
            return 0;
        } else if (x == 1) {
            return 1;
        } else {
            return fib(x - 2) + fib(x - 1);
        }
    };
    var expectedResult = fib(20);
    var runRequest = function() {
        $.ajax($('#url').attr("value"), {
            statusCode : {
                200 : function(data) {
                    try {
                        var obj = $.parseJSON(data);
                        $('#expected')
                                .html("Is " + obj.value + " == " + expectedResult);
                        if (obj.value == expectedResult) {
                            successes += 1;
                        } else {
                            errors += 1;
                        }
                    } catch(err) {
                        errors += 1;
                    }
                    $('#successes').html(successes);
                    $('#errors').html(errors);
                    $('#last_resp').html(
                            successes / ((new Date() - startTime) / 1000.0));
                    if (shouldRun) {
                        runRequest();
                    }
                },
                204 : function() {
                    // No content. I use this to indicate that we don't have a
                    // service. Log as error.
                    errors += 1;
                    $('#errors').html(errors);
                    $('#last_resp').html(
                            successes / ((new Date() - startTime) / 1000.0));
                    if (shouldRun) {
                        runRequest();
                    }
                }
            }
        });
    };
    $(function() {
        runRequest();
    });
    $('#url').change(function() {
        // shouldRun = false;
    });
    $('#stop_slow_calc').click(function() {
        $.ajax("/foo/stop/net.juckel.jruby.osgi.slowcalc");
    });
    $('#start_slow_calc').click(function() {
        $.ajax("/foo/start/net.juckel.jruby.osgi.slowcalc");
    });
    $('#toggle_test').click(function() {
        shouldRun = !shouldRun;
        // If we've turned it back on, reset timers and kick off first execution
        if (shouldRun) {
            successes = 0;
            errors = 0;
            startTime = new Date();
            var url = $('#url').attr("value");
            var index = url.lastIndexOf('/');
            var param = parseInt(url.substring(index + 1));
            expectedResult = fib(parseInt(url.substring(index + 1)));
            setTimeout(runRequest, 1);
        }
    });
})(jQuery);