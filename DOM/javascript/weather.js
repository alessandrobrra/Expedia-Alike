function addWeather(latitude, longitude) {
    fetch('https://api.open-meteo.com/v1/forecast?latitude=' + latitude + "&longitude=" + longitude + "&current_weather=true", {method: 'get'}).then(res => res.text()).then(data => {
            var json = JSON.parse(data);
            var weather = json.current_weather;
            var temperature = weather.temperature;
            var windspeed = weather.windspeed;
            var str = "Temperature: " + temperature + "C<br>Wind Speed: " + windspeed + "km/h";
            document.getElementById("weather").innerHTML = str;
        }
    ).catch(err => {
            console.log(err);
        }
    );
}