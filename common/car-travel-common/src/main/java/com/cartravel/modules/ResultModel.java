package com.cartravel.modules;

/**
 * 封装java中台向前端返回的数据类型
 * @param <T>
 */
public class ResultModel<T> {
	 
	private boolean success;
	private String msg;
	private T data;
	

	public ResultModel() {
		success = false;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
