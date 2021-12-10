package io.tesler.constgen;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@NoArgsConstructor
@GeneratesDtoMetamodel
public class TaskDTO {

	private int test;

	private long test1;

	private char test2;

	private boolean test3;

	private float test4;

	private double test5;

	private String test6;

	private Integer test7;

	private Long test8;

	private Boolean test9;

	private Float test10;

	private Double test11;

	private DtoField test12;

	private List<String> test13;

	private boolean test14;

	private boolean test15;

	public int getTest() {
		return this.test;
	}

	public long getTest1() {
		return this.test1;
	}

	public char getTest2() {
		return this.test2;
	}

	public boolean isTest3() {
		return this.test3;
	}

	public float getTest4() {
		return this.test4;
	}

	public double getTest5() {
		return this.test5;
	}

	public String getTest6() {
		return this.test6;
	}

	public Integer getTest7() {
		return this.test7;
	}

	public Long getTest8() {
		return this.test8;
	}

	public Boolean getTest9() {
		return this.test9;
	}

	public Float getTest10() {
		return this.test10;
	}

	public Double getTest11() {
		return this.test11;
	}

	public DtoField getTest12() {
		return this.test12;
	}

	public List<String> getTest13() {
		return this.test13;
	}

	public boolean getTest14() {
		return this.test14;
	}

}
