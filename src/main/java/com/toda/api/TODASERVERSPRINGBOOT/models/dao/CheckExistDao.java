package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import lombok.*;

@Getter
public final class CheckExistDao extends AbstractModel implements BaseModel {
    private int exist;

    public CheckExistDao(){}

    @Builder
    public CheckExistDao(int exist){
        this.exist = exist;
    }

    @Override
    public String toString(){
        if(exist == 1) return "value is exist";
        else return "value is not exist";
    }
}
