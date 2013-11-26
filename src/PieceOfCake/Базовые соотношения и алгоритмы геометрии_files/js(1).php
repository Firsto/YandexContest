    var datetime = {
        gmt: (typeof datetime_zone != 'undefined') ? datetime_zone : 2,
        time: 1385350626,
        run: function() {
            var t = this;
            setInterval(function() {
                t.update();
            }, 1000);
        },
        width: function(val, width, fill) {
            if(fill == undefined) fill = '0';
            var str = new String(val);
            if(str.length < width)
                for(var i = 0; i < width - str.length; i++)
                     str = fill + str;
            return str;
        },
        update: function() {
            this.time++;
            si = this.time % 60;
            mi = parseInt(this.time / 60) % 60;
            hi = parseInt(this.time / 3600 + parseInt(this.gmt)) % 24 ;
            $('#time').text(this.width(hi, 2) + ':' + this.width(mi, 2) + ':' + this.width(si, 2));
        }
    };

    datetime.run();


