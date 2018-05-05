package org.study.demo.service.impl;

import framework.annotation.Service;
import org.study.demo.service.ModifyService;
@Service
public class ModifyServiceImpl implements ModifyService {
    public void edit(String accountName){
        System.out.printf("执行编辑逻辑......");
    }
}
