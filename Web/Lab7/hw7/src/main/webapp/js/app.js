window.notify = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "success"
    });
}

window.ajax = function (data, $error) {
    $.ajax({
        type: "POST",
        dataType: "json",
        data,
        success: function (response) {
            if (!response["error"]) {
                location.href = response["redirect"];
            } else {
                $error.text(response["error"]);
            }
        }
    })
}