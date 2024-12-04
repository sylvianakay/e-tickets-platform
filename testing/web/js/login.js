$(document).ready(function() {

    $('input, select').on('input change', function() {
        if (!this.checkValidity()) {
            $(this).addClass('is-invalid').removeClass('is-valid');
        } else {
            $(this).addClass('is-valid').removeClass('is-invalid');
        }
    });

    $('#type').on('change', function() {
        if ($(this).val() === 'volunteer') {
            $('#volunteer-fields').removeClass('d-none');
            $('#volunteer-type, #height, #weight').prop('required', true);
            $('#terms-message').text("Δηλώνω υπεύθυνα ότι ανήκω στο ενεργό δυναμικό των εθελοντών πυροσβεστών.");
        } else {
            $('#volunteer-fields').addClass('d-none');
            $('#volunteer-type, #height, #weight').prop('required', false);
            $('#terms-message').text("Συμφωνώ με τους Όρους Χρήσης.");
        }
    });

    function checkPasswordMatch() {
        const password = $('#password').val();
        const confirmPassword = $('#password-confirm').val();
        if (password !== confirmPassword) {
            $('#password-error').text("Passwords do not match.").removeClass('text-success').addClass('text-danger');
        } else {
            $('#password-error').text("Passwords match.").removeClass('text-danger').addClass('text-success');
        }
    }

    function togglePasswordVisibility(id) {
        const passwordField = $(`#${id}`);
        passwordField.attr("type", passwordField.attr("type") === "password" ? "text" : "password");
    }

    function checkPasswordStrength() {

        const password = $('#password').val();
        const strengthMessage = $('#password-strength');

        strengthMessage.removeClass('text-success text-warning text-danger');

        if (/fire|fotia|ethelontis|volunteer/i.test(password)) {
            strengthMessage.text("Weak: restricted words").addClass('text-danger');
            return;
        }

        const digits = password.replace(/[^0-9]/g, "").length;
        if (digits >= password.length / 2) {
            strengthMessage.text("Weak: numbers >= 50%").addClass('text-danger');
            return;
        }

        const chars = {};
        for (let char of password) {
            chars[char] = (chars[char] || 0) + 1;
            if (chars[char] >= password.length / 2) {
                strengthMessage.text("Weak: repetitive characters").addClass('text-danger');
                return;
            }
        }

        const hasUppercase = /[A-Z]/.test(password);
        const hasLowercase = /[a-z]/.test(password);
        const hasNumber = /\d/.test(password);
        const hasSymbol = /[!@#$%^&*]/.test(password);

        if (hasUppercase && hasLowercase && hasNumber && hasSymbol) {
            strengthMessage.text("Strong Password").addClass('text-success');
        } else if (password.length >= 8 && (hasLowercase && hasNumber && hasSymbol)) { //ypothetw apo ass1 paramenoyn ypoxrewtika peza symbols kai nums
            strengthMessage.text("Medium Password").addClass('text-warning');
        } else {
            strengthMessage.text("Weak Password").addClass('text-danger'); return;
        }
    }

    $('#password').on('input', function() {
        checkPasswordMatch();
        checkPasswordStrength();
    });

    $('#password-confirm').on('input', function() {
        checkPasswordMatch();
    });

    $('.toggle-password').on('click', function() {
        const target = $(this).data('target');
        togglePasswordVisibility(target);
    });

    $('form').on('submit', function(event) {
        event.preventDefault();
        const form = $(this);
        let formValid = true;

        form.find('input, select').each(function() {
            if (!this.checkValidity()) {
                $(this).addClass('is-invalid').removeClass('is-valid');
                formValid = false;
            } else {
                $(this).addClass('is-valid').removeClass('is-invalid');
            }
        });

        if ($('#type').val() === 'volunteer') {
            const birthdate = new Date($('#birthdate').val());
            const today = new Date();
            let age = today.getFullYear() - birthdate.getFullYear();
            const monthDifference = today.getMonth() - birthdate.getMonth();
            if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthdate.getDate())) {
                age--;
            }
            if (age < 18) {
                $('#birthdate').addClass('is-invalid').removeClass('is-valid');
                formValid = false;
            }
        }
        if ($('#password-strength').hasClass('text-danger')) {
            formValid = false;
            $('#password-strength').addClass('text-danger');
        }

        if (formValid) {
            const formData = {};
            form.serializeArray().forEach(item => formData[item.name] = item.value);
            
            if (lat && lon) {
                formData['lat'] = lat;
                formData['lon'] = lon;
            } else {
                $('#json').text("Geolocation data is missing. Verify location before submitting.").addClass('text-danger');
                return; // Prevent submission if lat/lon is missing
            }
            $('#json').text(JSON.stringify(formData, null, 2)).removeClass('text-danger');
        
            const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
            $.ajax({
    url: contextPath + '/RegisterUser', // This must match the @WebServlet mapping
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(formData), // Convert form data to JSON
    success: function () {
        $('#json').text('Registration successful!').removeClass('text-danger').addClass('text-success');
    },
    error: function (xhr) {
        if (xhr.status === 409) {
            $('#json').text('Duplicate entry detected: ' + xhr.responseText).removeClass('text-success').addClass('text-danger');
        } else {
            $('#json').text('Error: ' + xhr.responseText).removeClass('text-success');
        }
    }
});

        } else {
            $('#json').text("Form is not valid.").removeClass('text-success').addClass('text-danger');
        }
        
    });
    
//    function registerUser() {
//    const formData = {
//        username: $("#username").val(),
//        email: $("#email").val(),
//        password: $("#password").val(),
//        firstname: $("#firstname").val(),
//        lastname: $("#lastname").val(),
//        country: $("#country").val()
//    };
//
//    $.ajax({
//        url: "RegisterUser",
//        type: "POST",
//        data: JSON.stringify(formData),
//        contentType: "application/json",
//        success: function () {
//            alert("Registration successful!");
//            $("#registrationForm")[0].reset();
//            $(".invalid-feedback").hide();
//            $("input").removeClass("is-invalid");
//        },
//        error: function (xhr) {
//            alert("Error: " + xhr.responseText);
//        }
//    });
//}


    function setPosition(lat, lon) {
        var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
        var toProjection = new OpenLayers.Projection("EPSG:900913");   // to Spherical Mercator Projection
        return new OpenLayers.LonLat(lon, lat).transform(fromProjection, toProjection);
    }

    function handler(position, message) {
        var popup = new OpenLayers.Popup.FramedCloud("Popup",
            position, null,
            message, null,
            true
        );
        map.addPopup(popup);
    }

    var lat, lon;
    let map;
    let markers;

    function initializeMap() {
        map = new OpenLayers.Map("Map");
        const mapnik = new OpenLayers.Layer.OSM();
        map.addLayer(mapnik);

        markers = new OpenLayers.Layer.Markers("Markers");
        map.addLayer(markers);

        const defaultPosition = setPosition(35.3387, 25.1442); // Heraklion, Crete
        map.setCenter(defaultPosition, 11);
    }

    $('#verify-location').on('click', function () {

        const country = $('#country').val();
        const municipality = $('#municipality').val();
        const address = $('#address').val();
        const fullAddress = `${address}, ${municipality}, ${country}`;
        $('#location-error').text("");

        const requestURL = `https://forward-reverse-geocoding.p.rapidapi.com/v1/forward?format=json&street=${encodeURIComponent(address)}&city=${encodeURIComponent(municipality)}&country=${encodeURIComponent(country)}&addressdetails=1&limit=1`;

        fetch(requestURL, {
            method: 'GET',
            headers: {
                'x-rapidapi-key': '8d44570724mshc88958962388edep16412ajsn090873638dff',
                'x-rapidapi-host': 'forward-reverse-geocoding.p.rapidapi.com'
            },
            mode: 'cors'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(response => {
                console.log("Geocoding Response: ", response); // Log response for debugging
                if (response && response.length > 0) {
                    const latitude = response[0].lat;
                    const longitude = response[0].lon;
                    const displayName = response[0].display_name;

                    if (displayName.includes("Κρήτη") || displayName.includes("Crete")) {

                        const position = setPosition(latitude, longitude);

                        $('#json').text(`lat: ${latitude}, lon: ${longitude}`).removeClass('text-danger').addClass('text-success');
                        lat = latitude; lon = longitude;
                        $('#show-map-button').removeClass('d-none');

                        $('#show-map').off('click').on('click', function () {

                            if (!map) {
                                initializeMap();
                            }

                            markers.clearMarkers();

                            const mar = new OpenLayers.Marker(position);
                            markers.addMarker(mar);
                            mar.events.register('mousedown', mar, function () {
                                handler(position, `Location: ${fullAddress}`);
                            });

                            map.setCenter(position, 12);
                            $('#Map').show();
                        });
                    } else {
                        $('#location-error').text("Η υπηρεσία είναι διαθέσιμη μόνο στην Κρήτη.").removeClass('text-success').addClass('text-danger');
                        $('#show-map-button').addClass('d-none');
                        $('#Map').hide();
                    }
                } else {
                    $('#location-error').text("Address not found. Please check the information entered.").removeClass('text-success').addClass('text-danger');
                    $('#show-map-button').addClass('d-none');
                    $('#Map').hide();
                }
            })
            .catch(error => {
                console.error("Error fetching geolocation data: ", error);
                $('#location-error').text("An error occurred while fetching geolocation data. Please try again.").removeClass('text-success').addClass('text-danger');
                $('#show-map-button').addClass('d-none');
                $('#Map').hide();
            });
    });
m
    initializeMap();

});
