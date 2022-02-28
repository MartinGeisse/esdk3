
#ifndef __FIRMWARE_INTERNAL_TERM_H__
#define __FIRMWARE_INTERNAL_TERM_H__

void termInitialize(void);
void termPrintString(const char *s);
void termPrintChar(char c);
void termPrintInt(int i);
void termPrintUnsignedInt(unsigned int i);
void termPrintHexInt(int i);
void termPrintUnsignedHexInt(unsigned int i);
void termPrintln(void);
void termPrintlnString(const char *s);
void termPrintlnChar(char c);
void termPrintlnInt(int i);
void termPrintlnUnsignedInt(unsigned int i);
void termPrintlnHexInt(int i);
void termPrintlnUnsignedHexInt(unsigned int i);

#endif
