package org.funql.ri.test.genericobject


enum class Types {string; int; float; bool; date; ref; obj; array; key} // key == reference into some collection, but not a database ref
fun str(name: String) = FieldDef(name, Types.string)
fun int(name: String) = FieldDef(name, Types.int)
fun float(name: String) = FieldDef(name, Types.float)
fun bool(name: String) = FieldDef(name, Types.bool)
fun date(name: String) = FieldDef(name, Types.date)
fun ref(name: String) = FieldDef(name, Types.ref)
fun obj(name: String) = FieldDef(name, Types.obj)
fun key(name: String) = FieldDef(name, Types.key)
fun arr(name: String) = FieldDef(name, Types.array)

class FieldDef(val name: String, val typ: Types)
{
    var refType: TypeDef? = null
}

class TypeDef(val name: String, vararg fieldsarg: FieldDef)
{
    val fields: Array<FieldDef> = fieldsarg
    fun get(s: String): FieldDef {
        val ret = fields.firstOrNull { s == it.name }
        if (ret == null)
            throw IllegalArgumentException("field $s not found in type $name")
        return ret
    }
}

class Key(val target: TestObject)
class Ref(val target: TestObject, val container: String)
class FunqlDate(val year: Int, val month: Int, val day: Int)

class TestObject(val typ: TypeDef, vararg valuesarg: Any?)
{

    class object {
        var genOid: Long = 1
            get() = $genOid++
    }


    val values: Array<Any?> = valuesarg
    val oid : Long = genOid

    fun forEach(f: (Any?) -> Unit) {
        values.forEach(f)
    }

    fun get(ix:Int) = values[ix]

}
