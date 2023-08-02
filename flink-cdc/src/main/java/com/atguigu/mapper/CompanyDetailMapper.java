package com.atguigu.mapper;

import com.atguigu.entity.CompanyDetailEntity;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface CompanyDetailMapper {

    List<CompanyDetailEntity> queryAll();

}
