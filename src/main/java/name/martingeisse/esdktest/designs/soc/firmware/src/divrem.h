
#ifndef __FIRMWARE_INTERNAL_DIVREM_H__
#define __FIRMWARE_INTERNAL_DIVREM_H__

unsigned int udivrem(unsigned int x, unsigned int y, int rem);
unsigned int udiv(unsigned int x, unsigned int y);
unsigned int urem(unsigned int x, unsigned int y);
int div(int x, int y);

#endif
