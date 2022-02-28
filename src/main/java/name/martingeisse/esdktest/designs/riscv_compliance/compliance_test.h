#ifndef __COMPLIANCE_TEST_H__
#define __COMPLIANCE_TEST_H__

#define RV_COMPLIANCE_HALT                                                    \
        .global grift_stop_addr;                                              \
        grift_stop_addr:                                                      \
        RVTEST_PASS                                                           \

#define RV_COMPLIANCE_RV32M                                                   \
        RVTEST_RV32M                                                          \

#define RV_COMPLIANCE_CODE_BEGIN                                              \
        RVTEST_CODE_BEGIN                                                     \

#define RV_COMPLIANCE_CODE_END                                                \
        RVTEST_CODE_END                                                       \

#define RV_COMPLIANCE_DATA_BEGIN                                              \
        RVTEST_DATA_BEGIN \

#define RV_COMPLIANCE_DATA_END                                                \
        RVTEST_DATA_END \

#endif
