
#ifndef __FIRMWARE_EXPORTED_PROFILING_H__
#define __FIRMWARE_EXPORTED_PROFILING_H__

extern void (*profReset)();
extern void (*profLog)(const char *label);
extern void (*profDisplay)();

#endif
