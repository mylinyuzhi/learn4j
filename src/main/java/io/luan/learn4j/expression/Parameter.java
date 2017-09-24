package io.luan.learn4j.expression;

import io.luan.learn4j.compute.ComputeNode;
import io.luan.learn4j.compute.impl.ParameterNode;
import lombok.Getter;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Parameter is holder for parameters to be updated on each gradient descend run
 * <p>
 * This is equivalent to TensorFlow.Variable
 *
 * @author Guangmiao Luan
 * @since 28/08/2017.
 */
public class Parameter extends BaseExpression {

    public static final String TYPE = "Parameter";

    @Getter
    private INDArray value;

    public Parameter(String name, INDArray value) {
        super(name);
        this.value = value;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    ComputeNode createComputeNode() {
        return new ParameterNode(this.getName(), value);
    }
}
