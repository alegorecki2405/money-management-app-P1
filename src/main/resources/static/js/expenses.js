function calculateExpenses(expenses) {
    let perType = new Map();

    expenses.forEach(function (expense) {
        if (!perType.has(expense.type)) {
            perType.set(expense.type, expense.amount);
        } else {
            let previousAmount = perType.get(expense.type);
            perType.set(expense.type, previousAmount + expense.amount);
        }
    });
    var data = []
    var sum = 0;
    perType.forEach((value, key) => {
        sum += value;
        console.log(key);
    })
    perType.forEach((value, key) => {
        var percent = (value / sum) * 100
        data.push({y: percent, label: key})
    })
    return data;
}

function payExpense(expenseId) {
    fetch('/update-expense-date/' + expenseId, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                console.error('Failed to update expense date');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
