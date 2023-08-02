package com.atguigu.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class CJRZLBBQREntity implements Serializable {


    /**
     * 结算机构
     */
    private String JSJG;
    /**
     * 申报日期
     */
    private String SBRQ;
    /**
     * 申报序号
     */
    private String SBXH;
    /**
     * 中登参与人编码
     */
    private String CYRDM;
    /**
     * 出借人简称
     */
    private String CJRJC;
    /**
     * 出借人全称
     */
    private String CJRQC;
    /**
     * 出借人性质，
     * 0--证券公司-自营
     * 1--证券公司-资产管理
     * 2--信托公司
     * 3--证券投资基金
     * 4--社保基金
     * 5--保险公司
     * 6--企业年金
     * 7—QFII/RQFII
     * 8—一般机构
     * 9—个人 (暂不启用)
     * Z—其他
     */
    private String CJRXZ;
    /**
     * 出借人类别
     * 0--独立出借人
     * 1--非独立出借人
     */
    private String CJRLB;
    /**
     * 代理券商名称
     */
    private String DLQSMC;
    /**
     * 出借证券来源
     * 0—个人
     * 1--机构
     * 2—产品
     * 3—其他
     */
    private String CJZQLY;
    /**
     * 产品名称
     */
    private String CPMC;
    /**
     * 产品类型
     * 0—集合理财
     * 1—定向理财
     * 2—开放式股票基金
     * 3—开放式混合基金
     * 4—开放式债券基金
     * 5—指数基金
     * 6—证券投资信托
     * 7—组合投资信托
     * Z—其他
     */
    private String CPLX;
    /**
     * 产品截止时间
     */
    private String CPJZSJ;
    /**
     * 产品托管人
     */
    private String CPTGR;
    /**
     * 出借人证件类型
     * 0--营业执照
     * 2--身份证
     * 3--护照
     * 4--军官证
     * 9--户口簿
     * A--港澳台通行证
     * G--其他
     */
    private String ZJLX;
    /**
     * 出借人证件号码
     */
    private String ZJHM;
    /**
     * 证件有效起始日期
     */
    private String ZJQSRQ;
    /**
     * 证件有效截止日期
     */
    private String ZJJSRQ;
    /**
     * 证件发放机构
     */
    private String ZJFZDW;
    /**
     * 证件地址
     */
    private String ZJDZ	;
    /**
     * 法人代表名称
     */
    private String FRMC	;
    /**
     * 法人代表证件类型
     * 2--身份证
     * 3--护照
     * 4--军官证
     * 9--户口簿
     * A--港澳台通行证
     * G--其他
     */
    private String FRZJLX;
    /**
     * 法人代表证件号码
     */
    private String FRZJHM;
    /**
     * 法人代表证件发证机构
     */
    private String FRFZJG;
    /**
     * 注册资金
     */
    private String ZCZJ	;
    /**
     * 组织机构代码
     */
    private String ZJGDM;
    /**
     * 联系人名称
     */
    private String LXRMC;
    /**
     * 联系人证件类型
     * 2--身份证
     * 3--护照
     * 4--军官证
     * 9--户口簿
     * A--港澳台通行证
     * G--其他
     */
    private String LXRZJLX	;

    /**
     * 联系人证件号码
     */
    private String LXRZJHM;
    /**
     * 电话号码
     */
    private String LXRDHHM;
    /**
     * 手机号码
     */
    private String LXRSJHM;
    /**
     * 电子邮箱
     */
    private String LXRDZYX;
    /**
     * 传真
     */
    private String LXRCZ;
    /**
     * 邮政编码
     */
    private String LXRYB;
    /**
     * 联系地址
     */
    private String LXRLXDZ;
    /**
     * 出借人上海证券账户股东代码
     */
    private String SHGDDM;
    /**
     * 出借人上海证券帐户股东席位
     */
    private String SHTGXW;
    /**
     * 出借人深圳证券账户股东代码
     */
    private String SZGDDM;
    /**
     * 出借人深圳证券帐户股东席位
     */
    private String SZTGXW;
    /**
     * 出借人报备处理结果
     */
    private String CLJG	;

    /**
     * 发送日期
     */
    private String FSRQ	;

    @Override
    public String toString() {
        return
                "JSJG='" + JSJG + '\'' +
                ", SBRQ='" + SBRQ + '\'' +
                ", SBXH='" + SBXH + '\'' +
                ", CYRDM='" + CYRDM + '\'' +
                ", CJRJC='" + CJRJC + '\'' +
                ", CJRQC='" + CJRQC + '\'' +
                ", CJRXZ='" + CJRXZ + '\'' +
                ", CJRLB='" + CJRLB + '\'' +
                ", DLQSMC='" + DLQSMC + '\'' +
                ", CJZQLY='" + CJZQLY + '\'' +
                ", CPMC='" + CPMC + '\'' +
                ", CPLX='" + CPLX + '\'' +
                ", CPJZSJ='" + CPJZSJ + '\'' +
                ", CPTGR='" + CPTGR + '\'' +
                ", ZJLX='" + ZJLX + '\'' +
                ", ZJHM='" + ZJHM + '\'' +
                ", ZJQSRQ='" + ZJQSRQ + '\'' +
                ", ZJJSRQ='" + ZJJSRQ + '\'' +
                ", ZJFZDW='" + ZJFZDW + '\'' +
                ", ZJDZ='" + ZJDZ + '\'' +
                ", FRMC='" + FRMC + '\'' +
                ", FRZJLX='" + FRZJLX + '\'' +
                ", FRZJHM='" + FRZJHM + '\'' +
                ", FRFZJG='" + FRFZJG + '\'' +
                ", ZCZJ='" + ZCZJ + '\'' +
                ", ZJGDM='" + ZJGDM + '\'' +
                ", LXRMC='" + LXRMC + '\'' +
                ", LXRZJLX='" + LXRZJLX + '\'' +
                ", LXRZJHM='" + LXRZJHM + '\'' +
                ", LXRDHHM='" + LXRDHHM + '\'' +
                ", LXRSJHM='" + LXRSJHM + '\'' +
                ", LXRDZYX='" + LXRDZYX + '\'' +
                ", LXRCZ='" + LXRCZ + '\'' +
                ", LXRYB='" + LXRYB + '\'' +
                ", LXRLXDZ='" + LXRLXDZ + '\'' +
                ", SHGDDM='" + SHGDDM + '\'' +
                ", SHTGXW='" + SHTGXW + '\'' +
                ", SZGDDM='" + SZGDDM + '\'' +
                ", SZTGXW='" + SZTGXW + '\'' +
                ", CLJG='" + CLJG + '\'' +
                ", FSRQ='" + FSRQ + '\'' +
                '\n';
    }
}
