package com.yuefeng.personaltree.model;

import java.util.List;

public class TestBean {

    private boolean success;
    private String msgTitle;
    private List<MsgBean> msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public List<MsgBean> getMsg() {
        return msg;
    }

    public void setMsg(List<MsgBean> msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        /**
         * id : dg1954
         * pid : dg1549
         * orgShortName : 淮北城区一标
         * orgCode :
         * orgName :
         * principal :
         * principalTel :
         * fax :
         * address :
         * organ : []
         * vehicle : []
         */

        private String id;
        private String pid;
        private String orgShortName;
        private String orgCode;
        private String orgName;
        private String principal;
        private String principalTel;
        private String fax;
        private String address;
        private List<OrganBean> organ;
        private List<?> vehicle;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public void setOrgShortName(String orgShortName) {
            this.orgShortName = orgShortName;
        }

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getPrincipalTel() {
            return principalTel;
        }

        public void setPrincipalTel(String principalTel) {
            this.principalTel = principalTel;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<OrganBean> getOrgan() {
            return organ;
        }

        public void setOrgan(List<OrganBean> organ) {
            this.organ = organ;
        }

        public List<?> getVehicle() {
            return vehicle;
        }

        public void setVehicle(List<?> vehicle) {
            this.vehicle = vehicle;
        }

        public static class OrganBean {
            /**
             * id : 77619b61ffffffc9051247ee0f700840
             * pid : dg1954
             * orgShortName : 吸污车组
             * orgCode :
             * orgName :
             * principal :
             * principalTel :
             * fax :
             * address :
             * organ : []
             * vehicle : ["}]
             */

            private String id;
            private String pid;
            private String orgShortName;
            private String orgCode;
            private String orgName;
            private String principal;
            private String principalTel;
            private String fax;
            private String address;
            private List<?> organ;
            private List<VehicleBean> vehicle;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getOrgShortName() {
                return orgShortName;
            }

            public void setOrgShortName(String orgShortName) {
                this.orgShortName = orgShortName;
            }

            public String getOrgCode() {
                return orgCode;
            }

            public void setOrgCode(String orgCode) {
                this.orgCode = orgCode;
            }

            public String getOrgName() {
                return orgName;
            }

            public void setOrgName(String orgName) {
                this.orgName = orgName;
            }

            public String getPrincipal() {
                return principal;
            }

            public void setPrincipal(String principal) {
                this.principal = principal;
            }

            public String getPrincipalTel() {
                return principalTel;
            }

            public void setPrincipalTel(String principalTel) {
                this.principalTel = principalTel;
            }

            public String getFax() {
                return fax;
            }

            public void setFax(String fax) {
                this.fax = fax;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public List<?> getOrgan() {
                return organ;
            }

            public void setOrgan(List<?> organ) {
                this.organ = organ;
            }

            public List<VehicleBean> getVehicle() {
                return vehicle;
            }

            public void setVehicle(List<VehicleBean> vehicle) {
                this.vehicle = vehicle;
            }

            public static class VehicleBean {
                /**
                 * id : b8d0bcf2ffffffc91d62b9c81ce7ccb9
                 * registrationNO : 皖F37302(1号吸污车)
                 * terminalNO : 13301444667
                 * simNO : 1440007710838
                 * pid : 77619b61ffffffc9051247ee0f700840
                 * terminalTypeID : 601
                 * stateType : 2
                 * gpsTime : 1900-01-01
                 * oilMax : wss://videostream.car900.com/
                 * oilSum : CA166
                 * brandType :
                 * carType :
                 * frameNumber :
                 * engineNumber :
                 * masterID :
                 * password :
                 * driverID1 :
                 * driverID2 :
                 * limitedDate : 1900-01-01
                 * memo : 1个摄像头，刹车接取力器，黄牌
                 * hasvideo : 2
                 * gt :
                 * speed : 0
                 * obd : ACC关
                 * lat : 0
                 * lon : 0
                 * vname : CA166
                 * vpwd : 712a3210-edaa-461c-9a15-dc5b3b1bffb8
                 * videoip : wss://videostream.car900.com/
                 */

                private String id;
                private String registrationNO;
                private String terminalNO;
                private String simNO;
                private String pid;
                private String terminalTypeID;
                private String stateType;
                private String gpsTime;
                private String oilMax;
                private String oilSum;
                private String brandType;
                private String carType;
                private String frameNumber;
                private String engineNumber;
                private String masterID;
                private String password;
                private String driverID1;
                private String driverID2;
                private String limitedDate;
                private String memo;
                private String hasvideo;
                private String gt;
                private String speed;
                private String obd;
                private String lat;
                private String lon;
                private String vname;
                private String vpwd;
                private String videoip;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getRegistrationNO() {
                    return registrationNO;
                }

                public void setRegistrationNO(String registrationNO) {
                    this.registrationNO = registrationNO;
                }

                public String getTerminalNO() {
                    return terminalNO;
                }

                public void setTerminalNO(String terminalNO) {
                    this.terminalNO = terminalNO;
                }

                public String getSimNO() {
                    return simNO;
                }

                public void setSimNO(String simNO) {
                    this.simNO = simNO;
                }

                public String getPid() {
                    return pid;
                }

                public void setPid(String pid) {
                    this.pid = pid;
                }

                public String getTerminalTypeID() {
                    return terminalTypeID;
                }

                public void setTerminalTypeID(String terminalTypeID) {
                    this.terminalTypeID = terminalTypeID;
                }

                public String getStateType() {
                    return stateType;
                }

                public void setStateType(String stateType) {
                    this.stateType = stateType;
                }

                public String getGpsTime() {
                    return gpsTime;
                }

                public void setGpsTime(String gpsTime) {
                    this.gpsTime = gpsTime;
                }

                public String getOilMax() {
                    return oilMax;
                }

                public void setOilMax(String oilMax) {
                    this.oilMax = oilMax;
                }

                public String getOilSum() {
                    return oilSum;
                }

                public void setOilSum(String oilSum) {
                    this.oilSum = oilSum;
                }

                public String getBrandType() {
                    return brandType;
                }

                public void setBrandType(String brandType) {
                    this.brandType = brandType;
                }

                public String getCarType() {
                    return carType;
                }

                public void setCarType(String carType) {
                    this.carType = carType;
                }

                public String getFrameNumber() {
                    return frameNumber;
                }

                public void setFrameNumber(String frameNumber) {
                    this.frameNumber = frameNumber;
                }

                public String getEngineNumber() {
                    return engineNumber;
                }

                public void setEngineNumber(String engineNumber) {
                    this.engineNumber = engineNumber;
                }

                public String getMasterID() {
                    return masterID;
                }

                public void setMasterID(String masterID) {
                    this.masterID = masterID;
                }

                public String getPassword() {
                    return password;
                }

                public void setPassword(String password) {
                    this.password = password;
                }

                public String getDriverID1() {
                    return driverID1;
                }

                public void setDriverID1(String driverID1) {
                    this.driverID1 = driverID1;
                }

                public String getDriverID2() {
                    return driverID2;
                }

                public void setDriverID2(String driverID2) {
                    this.driverID2 = driverID2;
                }

                public String getLimitedDate() {
                    return limitedDate;
                }

                public void setLimitedDate(String limitedDate) {
                    this.limitedDate = limitedDate;
                }

                public String getMemo() {
                    return memo;
                }

                public void setMemo(String memo) {
                    this.memo = memo;
                }

                public String getHasvideo() {
                    return hasvideo;
                }

                public void setHasvideo(String hasvideo) {
                    this.hasvideo = hasvideo;
                }

                public String getGt() {
                    return gt;
                }

                public void setGt(String gt) {
                    this.gt = gt;
                }

                public String getSpeed() {
                    return speed;
                }

                public void setSpeed(String speed) {
                    this.speed = speed;
                }

                public String getObd() {
                    return obd;
                }

                public void setObd(String obd) {
                    this.obd = obd;
                }

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLon() {
                    return lon;
                }

                public void setLon(String lon) {
                    this.lon = lon;
                }

                public String getVname() {
                    return vname;
                }

                public void setVname(String vname) {
                    this.vname = vname;
                }

                public String getVpwd() {
                    return vpwd;
                }

                public void setVpwd(String vpwd) {
                    this.vpwd = vpwd;
                }

                public String getVideoip() {
                    return videoip;
                }

                public void setVideoip(String videoip) {
                    this.videoip = videoip;
                }
            }
        }
    }
}
