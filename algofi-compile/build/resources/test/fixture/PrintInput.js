const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.on('line', (input) => {
    console.log(`${input}`);
    rl.close();
});

rl.on('close', () => {
    process.exit(0);
});