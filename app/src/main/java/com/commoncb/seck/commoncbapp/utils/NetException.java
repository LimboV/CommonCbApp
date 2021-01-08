package com.commoncb.seck.commoncbapp.utils;

/**
 *
 * 山科通讯协议的系统错误类型。
 *
 * @author l412
 *
 */
public class NetException extends Exception {
	private static final long serialVersionUID = -6062637094832661452L;

	public int zt;
	public String sdesc;

	public NetException(int code, String desc) {
		super();
		zt = code;
		sdesc = desc;
	}

	@Override
	public String toString() {
		return sdesc;
	}
}
