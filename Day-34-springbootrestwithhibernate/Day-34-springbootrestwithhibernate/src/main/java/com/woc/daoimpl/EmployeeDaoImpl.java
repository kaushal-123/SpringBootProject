package com.woc.daoimpl;

import com.woc.dao.EmployeeDao;
import com.woc.daoimpl.assembler.EmployeeAssembler;
import com.woc.dto.EmployeeDTO;
import com.woc.hibernate.config.HibernateUtil;
import com.woc.hibernate.model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeDaoImpl implements EmployeeDao {
    @Autowired
    private EmployeeAssembler employeeAssembler;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeAssembler.toDomain(employeeDTO);
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();

            session.save(employee);

            transaction.commit();

            return employeeAssembler.fromDomain(employee);
        }catch (Exception e){
            e.printStackTrace();
            if(null != transaction){
                transaction.rollback();
            }
        }
        return null;
    }

    @Override
    public List<EmployeeDTO> fetchEmployeeByName(String name) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try(Session session = sessionFactory.openSession()){
            Query query = session.createQuery("from Employee e where e.name=:name");
            query.setParameter("name", name);
            List<Employee> list = query.list();

            List<EmployeeDTO> employeeDTOS = employeeAssembler.fromDomain(list);
            return employeeDTOS;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
