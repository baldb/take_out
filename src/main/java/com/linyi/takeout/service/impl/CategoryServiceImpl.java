package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.Category;
import com.linyi.takeout.service.CategoryService;
import com.linyi.takeout.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-07-13 18:48:58
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




