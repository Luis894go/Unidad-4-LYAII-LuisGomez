package com.luis.codelab;

import java.util.*;
import java.util.regex.*;

public class MiniCompiler {
    private static final Pattern DECL = Pattern.compile("(int|double)\\s+([A-Za-z_]\\w*)\\s*=\\s*(.+);");
    private static final Pattern ASSIGN = Pattern.compile("([A-Za-z_]\\w*)\\s*=\\s*(.+);");
    private static final Pattern PRINT = Pattern.compile("print\\s*\\(([^)]+)\\)\\s*;");

    public CompileResult compile(String src) {
        List<String> tok = new ArrayList<>(), err = new ArrayList<>(), ir = new ArrayList<>(), reg = new ArrayList<>(), asm = new ArrayList<>(), mach = new ArrayList<>(), mem = new ArrayList<>();
        LinkedHashMap<String, Symbol> table = new LinkedHashMap<>();
        int line = 0, temp = 1, address = 0x1000;
        for (String raw : src.split("\\R")) {
            line++;
            String s = raw.trim();
            if (s.isEmpty()) continue;
            tokenize(s, line, tok);
            Matcher d = DECL.matcher(s), a = ASSIGN.matcher(s), p = PRINT.matcher(s);
            if (d.matches()) {
                String type = d.group(1), name = d.group(2), expr = d.group(3);
                if (table.containsKey(name)) {
                    err.add("Línea " + line + ": variable duplicada '" + name + "'.");
                    continue;
                }
                checkExpr(expr, table, err, line);
                table.put(name, new Symbol(name, type, expr, address));
                ir.add("t" + temp + " = " + expr);
                ir.add(name + " = t" + temp);
                reg.add("R" + ((temp - 1) % 4 + 1) + " ← " + expr);
                asm.add("MOV R" + ((temp - 1) % 4 + 1) + ", " + expr);
                asm.add("STORE [" + String.format("0x%04X", address) + "], R" + ((temp - 1) % 4 + 1));
                mem.add(String.format("0x%04X", address) + "  |  " + name + "  |  " + type + "  |  " + expr);
                address += 4;
                temp++;
            } else if (p.matches()) {
                String v = p.group(1).trim();
                if (!table.containsKey(v)) err.add("Línea " + line + ": variable no declarada '" + v + "'.");
                ir.add("PRINT " + v);
                reg.add("R1 ← " + v);
                asm.add("LOAD R1, " + v);
                asm.add("OUT R1");
            } else if (a.matches()) {
                String n = a.group(1), e = a.group(2);
                if (!table.containsKey(n)) err.add("Línea " + line + ": variable no declarada '" + n + "'.");
                checkExpr(e, table, err, line);
                ir.add(n + " = " + e);
                reg.add("R1 ← " + e);
                asm.add("MOV R1, " + e);
                asm.add("STORE " + n + ", R1");
            } else err.add("Línea " + line + ": error sintáctico; revise ';', paréntesis o estructura.");
        }
        if (err.isEmpty()) {
            asm.add("HLT");
            for (String ins : asm) mach.add(encode(ins));
        } else {
            asm.clear();
            asm.add("; No se genera ensamblador final: existen errores.");
            mach.add("No generado por errores de compilación.");
        }
        return new CompileResult(tok, new ArrayList<>(table.values()), err, ir, reg, asm, mach, mem);
    }

    private String encode(String ins) {
        int op = Math.abs(ins.split(" ")[0].hashCode()) & 255;
        int arg = Math.abs(ins.hashCode()) & 65535;
        return String.format("%02X %04X   |   %8s %16s   |   %s", op, arg, Integer.toBinaryString(0x100 | op).substring(1), String.format("%16s", Integer.toBinaryString(0x10000 | arg).substring(1)), ins);
    }

    private void checkExpr(String e, Map<String, Symbol> t, List<String> er, int l) {
        if (e.matches(".*?/\\s*0(?:\\D|$).*")) er.add("Línea " + l + ": división entre cero.");
        Matcher m = Pattern.compile("[A-Za-z_]\\w*").matcher(e);
        while (m.find()) {
            String w = m.group();
            if (!t.containsKey(w)) er.add("Línea " + l + ": identificador no declarado '" + w + "'.");
        }
    }

    private void tokenize(String s, int l, List<String> out) {
        Matcher m = Pattern.compile("\\b(int|double|print)\\b|[A-Za-z_]\\w*|\\d+(?:\\.\\d+)?|[+\\-*/=();]").matcher(s);
        while (m.find()) out.add("L" + l + "  " + classify(m.group()) + "  →  " + m.group());
    }

    private String classify(String x) {
        if (Set.of("int", "double", "print").contains(x)) return "RESERVADA";
        if (x.matches("\\d+(\\.\\d+)?")) return "NÚMERO";
        if (x.matches("[+\\-*/=]")) return "OPERADOR";
        if (x.matches("[();]")) return "SÍMBOLO";
        return "IDENTIFICADOR";
    }
}
