
unsigned int udivrem(unsigned int x, unsigned int y, int rem) {
    unsigned int quotient = 0, remainder = 0;
    for (int i = 0; i < 32; i++) {
        remainder = (remainder << 1) | (x >> 31);
        x = x << 1;
        quotient = (quotient << 1);
        if (remainder >= y) {
            remainder -= y;
            quotient++;
        }
    }
    return rem ? remainder : quotient;
}

unsigned int udiv(unsigned int x, unsigned int y) {
    return udivrem(x, y, 0);
}

unsigned int urem(unsigned int x, unsigned int y) {
    return udivrem(x, y, 1);
}

int div(int x, int y) {
    int negative = 0;
    if (x < 0) {
        x = -x;
        negative = !negative;
    }
    if (y < 0) {
        y = -y;
        negative = !negative;
    }
    int result = udiv(x, y);
    return negative ? -result : result;
}
