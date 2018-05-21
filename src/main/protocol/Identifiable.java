package protocol;

/******************************************************************************
 * Identifiable interface to allow pretty printer to format inputs for any
 * protocol class.
 ******************************************************************************/
/**
 * Get name of identifiable, protocol in this case.
 * @Return identifier.
 */
public interface Identifiable {
	String getName();
}
