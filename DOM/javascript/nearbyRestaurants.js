function getRestaurants(latitude, longitude) {
    let key = "myP1mE4F3nkZ2qYPVswhhdIAPPZxAtz8";
    fetch(`https://www.mapquestapi.com/search/v2/radius?origin=${latitude},${longitude}&radius=5&maxMatches=5&ambiguities=ignore&hostedData=mqap.ntpois|group_sic_code=?|581208&outFormat=json&key=${key}`)
        .then(response => response.json())
        .then(data => {
            let restaurants = data.searchResults;
            let i = 0;
            let str = 'Nearby Restaurants:<br>';
            while (i < restaurants.length) {
                str = str + restaurants[i].fields.name + '<br>';
                i++;
            }
            document.getElementById("restaurants").innerHTML = str;
        })
        .catch(error => {
            console.log(error);
        });
}
