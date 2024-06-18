#include <stdio.h>

int main() {
    char input[100];
    fgets(input, sizeof(input), stdin);
    printf("%s", input);
    return 0;
}
