
$("#logout").submit(function (event){
    event.preventDefault();
    $.post(
        "/users/api/logout"
    ).done(function (data) {

        window.location.replace("/");

    }).fail(function (data) {

        console.log(data);
        writeError(data.responseText);

    });
});