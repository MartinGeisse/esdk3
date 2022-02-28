
#include "profiling.h"
#include "term.h"

typedef struct {
    unsigned int time;
    const char *label;
} ProfilingEntry;

static unsigned int startTime;
static int entryCount;
static ProfilingEntry entries[32];

void profReset() {
    startTime = *(unsigned int *)0x07000000;
    entryCount = 0;
}

void profLog(const char *label) {
    if (entryCount < 32) {
        entries[entryCount].time = (*(unsigned int *)0x07000000) - startTime;
        entries[entryCount].label = label;
        entryCount++;
    }
}

void profDisplay() {
    for (int i = 0; i < entryCount; i++) {
        termPrintUnsignedHexInt(entries[i].time);
        termPrintChar(' ');
        termPrintUnsignedHexInt(entries[i].time - (i == 0 ? 0 : entries[i - 1].time));
        termPrintChar(' ');
        termPrintlnString(entries[i].label);
    }
}
