package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.AddressBook;
import com.linyi.takeout.service.AddressBookService;
import com.linyi.takeout.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-07-20 15:42:33
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




