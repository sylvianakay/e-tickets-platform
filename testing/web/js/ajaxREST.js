$(document).ready(function () {
    /**
     * Add a New Incident
     */
    function addIncident() {
        const incident = {
            incident_type: $("#incident_type").val(),
            description: $("#description").val(),
            user_type: $("#user_type").val(),
            user_phone: $("#user_phone").val(),
            address: $("#address").val(),
            lat: $("#lat").val(),
            lon: $("#lon").val(),
            prefecture: $("#prefecture").val(),
            municipality: $("#municipality").val(),
        };

        $.ajax({
            url: "http://localhost:4567/incident",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(incident),
            success: function (response) {
                $("#output").html(`<p>Incident Added Successfully: ${response.message}</p>`);
            },
            error: function (xhr, status, error) {
                $("#output").html(`<p>Error: ${xhr.responseText || status}</p>`);
            },
        });
    }

    /**
     * Get Incidents by Type and Status
     */
    function getIncidents(type, status, municipality = "") {
        const url = municipality
            ? `http://localhost:4567/incidents/${type}/${status}?municipality=${municipality}`
            : `http://localhost:4567/incidents/${type}/${status}`;

        $.ajax({
            url: url,
            type: "GET",
            success: function (response) {
                if (response.length > 0) {
                    let output = `<table><tr><th>Type</th><th>Status</th><th>Description</th><th>Address</th></tr>`;
                    response.forEach((incident) => {
                        output += `<tr>
                            <td>${incident.incident_type}</td>
                            <td>${incident.status}</td>
                            <td>${incident.description}</td>
                            <td>${incident.address}</td>
                        </tr>`;
                    });
                    output += `</table>`;
                    $("#output").html(output);
                } else {
                    $("#output").html("<p>No incidents found matching the criteria.</p>");
                }
            },
            error: function (xhr, status, error) {
                $("#output").html(`<p>Error: ${xhr.responseText || status}</p>`);
            },
        });
    }

    /**
     * Event Handlers
     */
    // Add Incident
    $("#add-incident-form").on("submit", function (event) {
        event.preventDefault();
        addIncident();
    });

    // Search Incidents
    $("#search-incidents-form").on("submit", function (event) {
        event.preventDefault();
        const type = $("#incident_type_search").val();
        const status = $("#incident_status_search").val();
        const municipality = $("#municipality_search").val();
        getIncidents(type, status, municipality);
    });
});
