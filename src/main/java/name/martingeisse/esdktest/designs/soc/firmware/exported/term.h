
#ifndef __FIRMWARE_EXPORTED_TERM_H__
#define __FIRMWARE_EXPORTED_TERM_H__

extern void (*termInitialize)(void);
extern void (*termPrintString)(const char *s);
extern void (*termPrintChar)(char c);
extern void (*termPrintInt)(int i);
extern void (*termPrintUnsignedInt)(unsigned int i);
extern void (*termPrintHexInt)(int i);
extern void (*termPrintUnsignedHexInt)(unsigned int i);
extern void (*termPrintln)(void);
extern void (*termPrintlnString)(const char *s);
extern void (*termPrintlnChar)(char c);
extern void (*termPrintlnInt)(int i);
extern void (*termPrintlnUnsignedInt)(unsigned int i);
extern void (*termPrintlnHexInt)(int i);
extern void (*termPrintlnUnsignedHexInt)(unsigned int i);

#endif
