package io.tesler.constgen;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
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

}
