package dev.ashli.kindling.transpiler.codeblocks.normal

import dev.ashli.kindling.MalformedList
import dev.ashli.kindling.UnexpectedValue
import dev.ashli.kindling.Value
import dev.ashli.kindling.serializer.serialize
import dev.ashli.kindling.serializer.serializeArgs
import dev.ashli.kindling.transpiler.*
import dev.ashli.kindling.transpiler.codeblocks.header.DFHeader
import dev.ashli.kindling.transpiler.values.DFValue
import dev.ashli.kindling.transpiler.values.Variable

data class Control(val type: String, val params: List<DFValue>) : DFBlock("control", 2, type == "End" || type == "Return") {
    companion object {
        fun transpileFrom(input: Value, header: DFHeader): List<DFBlock> {
            val inpList = checkList(input)
            if (inpList.isEmpty()) throw MalformedList("CodeBlock", "(Identifier ...)", input)
            return when (checkIdent(inpList[0])) {
                "return" -> {
                    if (inpList.size == 2) {
                        listOf(
                            SetVar("=", listOf(Variable("^ret", VariableScope.LOCAL), checkVal(inpList[1], CheckContext(header, "set_var", "=")))),
                            SetVar("-=", listOf(Variable("^depth ${header.technicalName()}", VariableScope.LOCAL))),
                            Control("Return", emptyList())
                        )
                    } else if (inpList.size == 1) {
                        listOf(
                            SetVar("-=", listOf(Variable("^depth ${header.technicalName()}", VariableScope.LOCAL))),
                            Control("Return", emptyList())
                        )
                    } else throw MalformedList("CodeBlock", "(return List<Value>?)", input)
                }
                "yield" -> {
                    if (inpList.size != 2) throw MalformedList("CodeBlock", "(yield List<Value>)", input)
                    listOf(SetVar("=", listOf(Variable("^ret", VariableScope.LOCAL), checkVal(inpList[1], CheckContext(header, "set_var", "=")))))
                }
                "control" -> {
                    if (inpList.size != 3) throw MalformedList("CodeBlock", "(control String<Type> List<Arguments>)", input)
                    val action = checkStr(inpList[1])
                    listOf(Control(action, checkParams(inpList[2], CheckContext(header, "control", action))))
                }
                else -> throw UnexpectedValue("return, yield, or control", inpList[0])
            }
        }
    }
    override fun serialize() = "{" +
            """"id":"block",""" +
            """"block":"control",""" +
            """"args":${serializeArgs(params)},""" +
            """"action":${type.serialize()}""" +
            "}"
}