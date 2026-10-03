package com.luis.codelab;

import java.util.List;

public record CompileResult(List<String> tokens, List<Symbol> symbols, List<String> errors,
                            List<String> intermediate, List<String> registers, List<String> assembly,
                            List<String> machine, List<String> memory) {
}
