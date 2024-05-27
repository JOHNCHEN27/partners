package com.lncanswer.findingpartnersbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lncanswer.findingpartnersbackend.model.domain.Tag;
import com.lncanswer.findingpartnersbackend.service.TagService;
import com.lncanswer.findingpartnersbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author JohnChen
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-05-27 21:27:56
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




