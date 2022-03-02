#ifndef __COMPLIANCE_TEST_H__
#define __COMPLIANCE_TEST_H__

#define XLEN 32

#define RVMODEL_SET_MSW_INT
#define RVMODEL_CLEAR_MSW_INT
#define RVMODEL_CLEAR_MTIMER_INT
#define RVMODEL_CLEAR_MEXT_INT

#define RVMODEL_IO_INIT
#define RVMODEL_IO_WRITE_STR(_R, _STR)
#define RVMODEL_IO_CHECK()
#define RVMODEL_IO_ASSERT_GPR_EQ(_S, _R, _I)
#define RVMODEL_IO_ASSERT_SFPR_EQ(_F, _R, _I)
#define RVMODEL_IO_ASSERT_DFPR_EQ(_D, _R, _I)

#define RVMODEL_BOOT \
    la x1, complianceDataBegin; sw x1, -8(x0); \
    la x1, complianceDataEnd; sw x1, -12(x0)

#define RVMODEL_HALT \
    sw x0, -4(x0)

#define RVMODEL_DATA_BEGIN \
    .globl complianceDataBegin; complianceDataBegin:

#define RVMODEL_DATA_END \
    .globl complianceDataEnd; complianceDataEnd:

#endif
