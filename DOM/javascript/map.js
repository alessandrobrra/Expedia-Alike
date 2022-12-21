function addToMapInfo(latitude, longitude) {
    fetch('', {method: 'get'}).then(res => res.text()).then(data => {
            const map = L.map('map').setView([latitude, longitude], 13);
            const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            }).addTo(map);
            var marker = L.marker([latitude, longitude]).addTo(map);
        }
    ).catch(err => {
            console.log(err);
        }
    );
}
