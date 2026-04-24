SRC_DIR = src/main/java
OUT_DIR = out
MAIN = com.quizleaderboard.Main
REG_NO ?= 2024CS101

all: compile run

compile:
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(shell find $(SRC_DIR) -name "*.java")
	@echo "Compiled successfully."

run:
	@java -cp $(OUT_DIR) $(MAIN) $(REG_NO)

clean:
	@rm -rf $(OUT_DIR)
	@echo "Cleaned."