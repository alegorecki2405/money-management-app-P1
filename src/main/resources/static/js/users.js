function deleteUser(userEmail) {
    fetch('/admin/delete/' + userEmail, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                console.error('Failed to delete user');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}