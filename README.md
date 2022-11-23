# DateOnlyParser

String to Date parser. Very strict, very fast and Java 1.1 compatible

Time is not supported

		 Date Date = new DateOnlyParser("yyyy-mm-dd")
		 	.parseExact("2022-11-22");

If you are using Java 1.8+ you should use LocalDate instead (unless you need the extra speed)

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y M d");
		LocalDate date = LocalDate.parse("2022 11 19", formatter);

