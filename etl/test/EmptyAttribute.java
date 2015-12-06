/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etl.test;

public class EmptyAttribute extends Attribute{

	public EmptyAttribute() {
		super("", "", "");
	}

	public boolean isEmpty(){
		return true;
	}
}