package com.edu.hutech.controllers;

import com.edu.hutech.dtos.AjaxResponse;
import com.edu.hutech.entities.Trainee;
import com.edu.hutech.entities.Trainer;
import com.edu.hutech.models.PaginationRange;
import com.edu.hutech.repositories.AttendanceRepository;
import com.edu.hutech.repositories.TraineeRepository;
import com.edu.hutech.services.implementation.RoleService;
import com.edu.hutech.services.implementation.TraineeCourseService;
import com.edu.hutech.services.implementation.TraineeServiceImpl;
import com.edu.hutech.services.implementation.UserServiceImpl;
import com.edu.hutech.utils.page.Pagination;
import com.edu.hutech.utils.sort.GenericComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@Transactional
@RequestMapping("/trainee-management")
public class TraineeController {
    @Autowired
    TraineeRepository traineeRepository;

    @Autowired
    TraineeServiceImpl traineeService;

    @Autowired
    RoleService roleService;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    TraineeCourseService traineeCourseService;

    @Autowired
    UserServiceImpl userService;


    @GetMapping()
    public String displayTraineeManagement(Model model,final HttpServletRequest request,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("field") Optional<String> field,
                                           @RequestParam("view") Optional<String> view,
                                           @RequestParam("search") Optional<String> search) {

        int cPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortField = field.orElse("default");
        String keyword = search.orElse("");
        String modeView = view.orElse("list");


        pageSize = pageSize < 5 ? 5 : Math.min(pageSize, 500);

        List<Trainee> listTrainees = traineeRepository.findScoreByAllTrainee();

        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);
            Collections.sort(listTrainees, new GenericComparator(true, splits[0]));
        } else {
            if (sortField.equals("default")) {
            } else {
                Collections.sort(listTrainees, new GenericComparator(false, sortField));
            }
        }

        List<Trainee> trainees = Pagination.getPage(listTrainees, cPage, pageSize);


        int totalPages = (int) Math.ceil((double) listTrainees.size() / (double) pageSize);

        HttpSession session = request.getSession();
        if(session.getAttribute("message") != null){
            model.addAttribute("message", "Update success!");
        }
        session.removeAttribute("message");

        model.addAttribute("modeView", modeView);
        model.addAttribute("trainees", trainees);
        model.addAttribute("cPage", cPage);
        model.addAttribute("size", pageSize);
        model.addAttribute("totalElements", listTrainees.size());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("field", sortField);
        model.addAttribute("search", keyword);

        PaginationRange p = Pagination.paginationByRange(cPage, listTrainees.size(), pageSize, 5);
        model.addAttribute("paginationRange", p);

        return "pages/trainee-views/trainee-management";
    }

    @GetMapping("/add-trainee")
    public String addView(final ModelMap model, final HttpServletRequest request){
        model.addAttribute("message", "");
        String messsage = request.getParameter("message");
        if(messsage != null && messsage.equalsIgnoreCase("success")) {
            model.addAttribute("message", "<div class=\"alert alert-success\">" +
                    "  <strong>Success!</strong> Thêm mới thành công." +
                    "</div>");
        }
        model.addAttribute("trainee", new Trainee());
        return "pages/trainee-views/trainee-create-new";
    }

    @Transactional
    @PostMapping("/add-trainee")
    public String add(final ModelMap model,
                      @ModelAttribute("trainee") Trainee trainee) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        trainee.getUser().setPassword(encoder.encode(trainee.getUser().getPassword()));

        String account = trainee.getEmail();

        trainee.getUser().setAccount(account.substring(0, account.indexOf("@")));

        trainee.getUser().setRoles(roleService.findByName("ROLE_TRAINEE"));
        if(!userService.checkEmail(trainee.getEmail())){
            traineeService.save(trainee);
        }else {
            return null;
        }
        model.addAttribute("trainer", new Trainer());
        return "redirect:/trainee-management/add-trainee?message=success";
    }

    @Transactional
    @PostMapping("/update-trainee")
    public String update(final HttpServletRequest request,
                      @ModelAttribute("trainee") Trainee trainee) {
        traineeService.update(trainee);
        HttpSession session = request.getSession();
        session.setAttribute("message", "update");
        return "redirect:/trainee-management";
    }

    @PostMapping("/delete")
    public ResponseEntity<AjaxResponse> delete(@RequestBody Integer id) {
        traineeService.delete(id);
        return ResponseEntity.ok(new AjaxResponse(200, "OK"));
    }

//@GetMapping("/export")
//    public void exportToExcel(HttpServletResponse response,
//                              @RequestParam("page") Optional<Integer> page,
//                              @RequestParam("size") Optional<Integer> size,
//                              @RequestParam("field") Optional<String> field) throws IOException {
//
//        int cPage = page.orElse(1);
//        int pageSize = size.orElse(10);
//        String sortField = field.orElse("default");
//
//
//        pageSize = pageSize < 5 ? 5 : pageSize > 500 ? 500 : pageSize;
//
//        List<TraineeScoreDto> listTrainees = traineeRepository.findScoreByAllTrainee();
//
//        if (sortField.contains("-asc")) {
//            String[] splits = sortField.split("-asc", 2);
//            Collections.sort(listTrainees, new GenericComparator(true, splits[0]));
//        } else {
//            Collections.sort(listTrainees, new GenericComparator(false, sortField));
//        }
//
//        List<TraineeScoreDto> trainees = Pagination.getPage(listTrainees, cPage, pageSize);
//
//        response.setContentType("application/octet-stream");
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=trainee-management_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//
//
//        List<TraineeDto> traineeDtoList = new ArrayList<>();
//
//        for (TraineeScoreDto tsd : trainees) {
//            traineeDtoList.add(new TraineeDto(tsd.getId(), tsd.getName(), tsd.getAccount(), tsd.getScore(), tsd.getEmail(), tsd.getUniversity()));
//        }
//
//
//        ExcelExporter<TraineeDto> excelExporter = new ExcelExporter<>(traineeDtoList, TraineeDto.class);
//
//        excelExporter.export(response);
//
//    }


    @GetMapping("/trainee-details")
    public String displayAllTraineeDetails(Model model, @RequestParam("id") Integer id) {

        Trainee trainee = traineeService.findById(id);

        double scale = Math.pow(10, 1);

        model.addAttribute("trainee", trainee);
//        model.addAttribute("presentAttendance", presentAttendance);
//        model.addAttribute("totalAttendance", totalAttendance);
//        model.addAttribute("listNameAndScore", listNameAndScore);
//        model.addAttribute("listDateAndAttendance", listDateAndAttendance);

        return "pages/trainee-views/trainee-details";
    }

}
