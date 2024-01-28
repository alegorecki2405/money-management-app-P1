function calculateReturn() {
    const initialAmount = parseFloat(document.getElementById('initialAmount').value);
    const contributionType = document.getElementById('contributionType').value;
    const contributionAmount = parseFloat(document.getElementById('contributionAmount').value);
    const numberOfYears = parseInt(document.getElementById('numberOfYears').value);
    const annualReturn = parseFloat(document.getElementById('annualReturn').value) / 100;

    let total = initialAmount;
    let amountAdded = initialAmount;
    let months = 0;
    if (contributionType === 'monthly') {
        months = numberOfYears * 12;
        for (let i = 0; i < months; i++) {
            total *= 1 + annualReturn / 12;
            total += contributionAmount;
            amountAdded += contributionAmount;
        }
    } else if (contributionType === 'yearly') {
        months = numberOfYears;
        for (let i = 0; i < months; i++) {
            total *= 1 + annualReturn;
            total += contributionAmount;
            amountAdded += contributionAmount;
        }
    }

    document.getElementById('result').innerText = `Investment Return: Total return after ${numberOfYears} years: ${total.toFixed(2)} the amount of money added ${amountAdded}`;
}