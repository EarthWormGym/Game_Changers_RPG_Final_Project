require "spec_helper"
feature "sign up" do
  scenario "I should be able to visit the sign up page" do
    visit '/sign_up'
    expect(page).to have_content 'Sign Up for Battle!'
  end
## need to delete database before running.
  scenario "I should be able to sign up" do
    visit '/sign_up'
    fill_in('username', with: 'zero')
    fill_in('full_name', with: 'Vikash Ramnarain')
    fill_in('password', with: '12345')
    click_button("Submit")
    expect(page).to have_content 'Thank you for signing up!'
  end
end
