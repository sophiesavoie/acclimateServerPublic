package hackqc18.Acclimate.exception;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";


    /**
     * Handler for OperationNotSupportedException, wraps the exception message
     * within a VndErrors and generates a FORBIDDEN HTTP status (403).
     *
     * @param ex the exception
     * @return a vendor error message to be displayed to the user
     */
    @ResponseBody
    @ExceptionHandler(OperationNotSupportedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public VndErrors operationNotSupportedExceptionHandler(HttpServletRequest req,
            OperationNotSupportedException ex) {
        return new VndErrors(DEFAULT_ERROR_VIEW, ex.getLocalizedMessage(), new Link(req.getRequestURL().toString()));
    }


    /**
     * Exception handler to manage all exceptions not annotated with a
     * ResponseStatus and not managed by another ExceptionHandler
     *
     * @param req the servlet request (automatically injected by spring)
     * @param e the exception being thrown
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public VndErrors defaultErrorHandler(HttpServletRequest req, Exception ex)
            throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(ex.getClass(),
                ResponseStatus.class) != null) {
            throw ex;
        }

        // Otherwise setup and send the user to a default error-view.
        return new VndErrors(DEFAULT_ERROR_VIEW, ex.getLocalizedMessage(), new Link(req.getRequestURL().toString()));
    }
//    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e)
//            throws Exception {
//        // If the exception is annotated with @ResponseStatus rethrow it and let
//        // the framework handle it.
//        // AnnotationUtils is a Spring Framework utility class.
//        if (AnnotationUtils.findAnnotation(e.getClass(),
//                ResponseStatus.class) != null) {
//            throw e;
//        }
//
//        // Otherwise setup and send the user to a default error-view.
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("exception", e);
//        mav.addObject("url", req.getRequestURL());
//        mav.setViewName(DEFAULT_ERROR_VIEW);
//        return mav;
//    }
}
