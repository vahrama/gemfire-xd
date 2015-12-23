/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <thrift/cxxfunctional.h>

#include "gfxd_struct_DateTime.h"

#include <algorithm>

namespace com { namespace pivotal { namespace gemfirexd { namespace thrift {

const char* DateTime::ascii_fingerprint = "56A59CE7FFAF82BCA8A19FAACDE4FB75";
const uint8_t DateTime::binary_fingerprint[16] = {0x56,0xA5,0x9C,0xE7,0xFF,0xAF,0x82,0xBC,0xA8,0xA1,0x9F,0xAA,0xCD,0xE4,0xFB,0x75};

uint32_t DateTime::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_secsSinceEpoch = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->secsSinceEpoch);
          isset_secsSinceEpoch = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  if (!isset_secsSinceEpoch)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t DateTime::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("DateTime");

  xfer += oprot->writeFieldBegin("secsSinceEpoch", ::apache::thrift::protocol::T_I64, 1);
  xfer += oprot->writeI64(this->secsSinceEpoch);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(DateTime &a, DateTime &b) {
  using ::std::swap;
  swap(a.secsSinceEpoch, b.secsSinceEpoch);
}

}}}} // namespace