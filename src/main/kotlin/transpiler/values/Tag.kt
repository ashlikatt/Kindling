package transpiler.values

import MalformedList
import Value
import serializer.serialize
import transpiler.*

data class Tag(val option: String, val tag: String, val block: String, val action: String, val variable: Variable?) : DFValue {
    companion object {
        fun transpileFrom(input: Value, context: CheckContext): Tag {
            val inpList = checkList(input)
            return if (inpList.size == 3) {
                Tag(checkStr(inpList[2]), checkStr(inpList[1]), context.blockType, context.blockAction, null)
            } else if (inpList.size == 4) {
                Tag(checkStr(inpList[2]), checkStr(inpList[1]), context.blockType, context.blockAction, Variable.transpileFrom(inpList[3], context))
            } else throw MalformedList("Value", "(tag String<Key> String<Value> List<Variable>?)", input)

        }
    }
    override fun serialize() = """{"id":"bl_tag","data":{"option":${option.serialize()},"tag":${tag.serialize()},"action":${
        action.serialize()
    },"block":${block.serialize()}""" +
    (if (variable != null) ""","variable":${variable.serialize()}""" else "") +
    """}}"""
}