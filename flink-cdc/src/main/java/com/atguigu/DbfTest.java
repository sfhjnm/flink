package com.atguigu;



import cn.hutool.core.util.CharsetUtil;
import com.atguigu.entity.CJRZLBBQREntity;
import com.linuxense.javadbf.DBFReader;
import org.apache.commons.compress.utils.Lists;

import java.io.*;
import java.util.List;


public class DbfTest {

    public static void main_CJRZLBBQR(String[] args) {
        File dbfFile = new File("/Users/liqi/Downloads/100019_CJRZLBBQR.dbf");
        try {

            InputStream inputStream = new FileInputStream(dbfFile);
            DBFReader reader = new DBFReader(inputStream);
            reader.setCharactersetName(CharsetUtil.GBK);

            int recordCount = reader.getRecordCount();//纵向
            int fieldCount = reader.getFieldCount();//横向
            System.out.println("recordCount："+recordCount);
            System.out.println("fieldCount："+fieldCount);

            List<CJRZLBBQREntity> list= Lists.newArrayList();
            for (int i = 0; i < recordCount; i++) {
                CJRZLBBQREntity dbfEntity = new CJRZLBBQREntity();

                Object[] objects = reader.nextRecord();
                for (int j = 0; j < fieldCount; j++){
                    String value = objects[j].toString().trim();
                    if(j==0){
                        dbfEntity.setJSJG(value);
                    } else if (j==1) {
                        dbfEntity.setSBRQ(value);
                    } else if (j==2) {
                        dbfEntity.setSBXH(value);
                    } else if (j==3) {
                        dbfEntity.setCYRDM(value);
                    } else if (j==4) {
                        dbfEntity.setCJRJC(value);
                    } else if (j==5) {
                        dbfEntity.setCJRQC(value);
                    } else if (j==6) {
                        dbfEntity.setCJRXZ(value);
                    } else if (j==7) {
                        dbfEntity.setCJRLB(value);
                    } else if (j==8) {
                        dbfEntity.setDLQSMC(value);
                    } else if (j==9) {
                        dbfEntity.setCJZQLY(value);

                    } else if (j==10) {
                        dbfEntity.setCPMC(value);

                    } else if (j==11) {
                        dbfEntity.setCPLX(value);

                    } else if (j==12) {
                        dbfEntity.setCPJZSJ(value);

                    } else if (j==13) {
                        dbfEntity.setCPTGR(value);

                    } else if (j==14) {
                        dbfEntity.setZJLX(value);

                    } else if (j==15) {
                        dbfEntity.setZJHM(value);

                    } else if (j==16) {
                        dbfEntity.setZJQSRQ(value);

                    } else if (j==17) {
                        dbfEntity.setZJJSRQ(value);

                    } else if (j==18) {
                        dbfEntity.setZJFZDW(value);

                    } else if (j==19) {
                        dbfEntity.setZJDZ(value);

                    } else if (j==20) {
                        dbfEntity.setFRMC(value);

                    } else if (j==21) {
                        dbfEntity.setFRZJLX(value);

                    } else if (j==22) {
                        dbfEntity.setFRZJHM(value);

                    } else if (j==23) {
                        dbfEntity.setFRFZJG(value);

                    } else if (j==24) {
                        dbfEntity.setZCZJ(value);

                    } else if (j==25) {
                        dbfEntity.setZJGDM(value);

                    } else if (j==26) {
                        dbfEntity.setLXRMC(value);

                    } else if (j==27) {
                        dbfEntity.setLXRZJLX(value);

                    } else if (j==28) {
                        dbfEntity.setLXRZJHM(value);

                    } else if (j==29) {
                        dbfEntity.setLXRDHHM(value);//29

                    } else if (j==30) {
                        dbfEntity.setLXRSJHM(value);

                    } else if (j==31) {
                        dbfEntity.setLXRDZYX(value);

                    } else if (j==32) {
                        dbfEntity.setLXRCZ(value);

                    } else if (j==33) {
                        dbfEntity.setLXRYB(value);

                    } else if (j==34) {
                        dbfEntity.setLXRLXDZ(value);

                    } else if (j==35) {
                        dbfEntity.setSHGDDM(value);

                    } else if (j==36) {
                        dbfEntity.setSHTGXW(value);

                    } else if (j==37) {
                        dbfEntity.setSZGDDM(value);

                    } else if (j==38) {
                        dbfEntity.setSZTGXW(value);

                    } else if (j==39) {
                        dbfEntity.setCLJG(value);

                    } else if (j==40) {
                        dbfEntity.setFSRQ(value);
                    }
                }
                list.add(dbfEntity);
            }
            System.out.println("list："+list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
