
#ifndef __FIRMWARE_EXPORTED_DIVREM_H__
#define __FIRMWARE_EXPORTED_DIVREM_H__

extern unsigned int (*udivrem)(unsigned int x, unsigned int y, int rem);
extern unsigned int (*udiv)(unsigned int x, unsigned int y);
extern unsigned int (*urem)(unsigned int x, unsigned int y);
extern int (*div)(int x, int y);

#endif
