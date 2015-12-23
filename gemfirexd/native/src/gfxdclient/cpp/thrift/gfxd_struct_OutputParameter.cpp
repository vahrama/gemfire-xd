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

#include "gfxd_struct_OutputParameter.h"

#include <algorithm>

namespace com { namespace pivotal { namespace gemfirexd { namespace thrift {

const char* OutputParameter::ascii_fingerprint = "69ED04DC035A4CF78CE4B470902F18B3";
const uint8_t OutputParameter::binary_fingerprint[16] = {0x69,0xED,0x04,0xDC,0x03,0x5A,0x4C,0xF7,0x8C,0xE4,0xB4,0x70,0x90,0x2F,0x18,0xB3};

uint32_t OutputParameter::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_type = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          int32_t ecast236;
          xfer += iprot->readI32(ecast236);
          this->type = (GFXDType::type)ecast236;
          isset_type = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->scale);
          this->__isset.scale = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->typeName);
          this->__isset.typeName = true;
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

  if (!isset_type)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t OutputParameter::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("OutputParameter");

  xfer += oprot->writeFieldBegin("type", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32((int32_t)this->type);
  xfer += oprot->writeFieldEnd();

  if (this->__isset.scale) {
    xfer += oprot->writeFieldBegin("scale", ::apache::thrift::protocol::T_I32, 2);
    xfer += oprot->writeI32(this->scale);
    xfer += oprot->writeFieldEnd();
  }
  if (this->__isset.typeName) {
    xfer += oprot->writeFieldBegin("typeName", ::apache::thrift::protocol::T_STRING, 3);
    xfer += oprot->writeString(this->typeName);
    xfer += oprot->writeFieldEnd();
  }
  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(OutputParameter &a, OutputParameter &b) {
  using ::std::swap;
  swap(a.type, b.type);
  swap(a.scale, b.scale);
  swap(a.typeName, b.typeName);
  swap(a.__isset, b.__isset);
}

}}}} // namespace