package org.funql.ri.data;

import org.funql.ri.util.NamedImpl;

/**
 * Created by hmf on 23.11.13.
 */
public abstract class FunqlTypedField extends NamedImpl implements FunqlField {
    private final FunqlTypeDef subType;

    public FunqlTypedField(String name, FunqlTypeDef subType) {
	super(name);
	this.subType = subType;
    }

    @Override
    public FunqlTypeDef getSubType() {
	return subType;
    }
    public static class RefField extends FunqlTypedField {
	public RefField(String name, FunqlTypeDef subType) {
	    super(name, subType);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Ref;
	}
    }
    public static class ListField extends FunqlTypedField {
	public ListField(String name, FunqlTypeDef subType) {
	    super(name, subType);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.List;
	}
    }
    public static class NestedObjectField extends FunqlTypedField {
	public NestedObjectField(String name, FunqlTypeDef subType) {
	    super(name, subType);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Object;
	}
    }
}
