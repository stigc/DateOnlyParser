# DateOnlyParser

String to Date parser. Very strict, very fast and Java 1.1 compatible

Time is not supported

		 Date Date = new DateOnlyParser("yyyy-mm-dd")
		 	.parseExact("2022-11-22");
