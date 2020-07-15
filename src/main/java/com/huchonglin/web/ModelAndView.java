package com.huchonglin.web;

/**
 * 参数和视图的包装类
 * 
 * @author: hcl
 * @date: 2020/7/5 10:25
 */
public class ModelAndView {
    private String view;
    private Object model;

    public ModelAndView() {
    }

    public ModelAndView(String view, Object model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Object getModel() {
        return model;
    }

    public String getModelName() {
        return model.toString();
    }

    public void setModel(Object model) {
        this.model = model;
    }

}
